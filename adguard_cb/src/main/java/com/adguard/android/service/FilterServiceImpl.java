/*
 This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2018 AdGuard Content Blocker. All rights reserved.

 AdGuard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 AdGuard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.adguard.android.contentblocker.receiver.AlarmReceiver;
import com.adguard.android.contentblocker.FilterUpdateJobService;
import com.adguard.android.contentblocker.R;
import com.adguard.android.ServiceLocator;
import com.adguard.android.contentblocker.db.FilterListDao;
import com.adguard.android.contentblocker.db.FilterListDaoImpl;
import com.adguard.android.contentblocker.db.FilterRuleDao;
import com.adguard.android.contentblocker.db.FilterRuleDaoImpl;
import com.adguard.android.contentblocker.ServiceApiClient;
import com.adguard.android.contentblocker.model.FilterList;
import com.adguard.android.commons.network.NetworkUtils;
import com.adguard.android.commons.concurrent.DispatcherThreadPool;
import com.adguard.android.commons.io.IoUtils;
import com.adguard.android.commons.network.InternetUtils;
import com.adguard.android.commons.web.UrlUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Filter service implementation.
 */
public class FilterServiceImpl extends BaseUiService implements FilterService {
    private static final Logger LOG = LoggerFactory.getLogger(FilterServiceImpl.class);

    public static final int SHOW_USEFUL_ADS_FILTER_ID = 10;

    private static final int MIN_RULE_LENGTH = 4;
    private static final String ASCII_SYMBOL = "\\p{ASCII}+";
    private static final String COMMENT = "!";
    private static final String ADBLOCK_META_START = "[Adblock";
    private static final String MASK_OBSOLETE_SCRIPT_INJECTION = "###adg_start_script_inject";
    private static final String MASK_OBSOLETE_STYLE_INJECTION = "###adg_start_style_inject";

    private static final int UPDATE_INVALIDATE_PERIOD = 24 * 60 * 60 * 1000; // 24 hours
    private static final int UPDATE_INTERVAL_PERIOD = 60 * 60 * 1000; // 1 hours
    private static final int UPDATE_INITIAL_PERIOD = 60 * 1000; // 1 minute

    private static final String FILTERS_UPDATE_QUEUE = "filters-update-queue";
    private static final int JOB_ID = 322;

    private final Context context;
    private final FilterListDao filterListDao;
    private final FilterRuleDao filterRuleDao;
    private final PreferencesService preferencesService;

    private boolean reloadBrowserChache = false;
    private int cachedFilterRuleCount = 0;

    /**
     * Creates an instance of AdguardService
     *
     * @param context Context
     */
    public FilterServiceImpl(Context context) {
        LOG.info("Creating AdguardService instance for {}", context);
        this.context = context;
        filterListDao = new FilterListDaoImpl(context);
        filterRuleDao = new FilterRuleDaoImpl(context);
        preferencesService = ServiceLocator.getInstance(context).getPreferencesService();
    }

    public static void enableContentBlocker(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_UPDATE");
        intent.setData(Uri.parse("package:com.adguard.android.contentblocker"));
        context.sendBroadcast(intent);
    }

    @Override
    public void checkFiltersUpdates(Activity activity) {
        LOG.info("Start manual filters updates check");
        ServiceLocator.getInstance(activity.getApplicationContext()).getPreferencesService().setLastUpdateCheck(new Date().getTime());

        ProgressDialog progressDialog = showProgressDialog(activity, R.string.checkUpdatesProgressDialogTitle, R.string.checkUpdatesProgressDialogMessage);
        DispatcherThreadPool.getInstance().submit(FILTERS_UPDATE_QUEUE, new CheckUpdatesTask(activity, progressDialog));
        LOG.info("Submitted filters update task");
    }

    @Override
    public List<FilterList> getFilters() {
        return filterListDao.selectFilterLists();
    }

    @Override
    public int getFilterListCount() {
        return filterListDao.getFilterListCount();
    }

    @Override
    public int getEnabledFilterListCount() {
        return filterListDao.getEnabledFilterListCount();
    }

    @Override
    public int getFilterRuleCount() {
        if (cachedFilterRuleCount == 0) {
            cachedFilterRuleCount = preferencesService.getFilterRuleCount();
        }
        return cachedFilterRuleCount;
    }

    @Override
    public List<FilterList> checkFilterUpdates(boolean force) {
        return checkOutdatedFilterUpdates(force);
    }

    @Override
    public void scheduleFiltersUpdate() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (jobScheduler != null) {
                if (!isJobCreated(jobScheduler, JOB_ID)) {
                    JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, FilterUpdateJobService.class))
                            .setPeriodic(UPDATE_INTERVAL_PERIOD)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .setPersisted(true);

                    if (jobScheduler.schedule(builder.build()) != JobScheduler.RESULT_SUCCESS) {
                        LOG.error("Error while create the filter update job!");
                    }
                } else {
                    LOG.info("Filters update is running");
                }
            }
        } else {
            Intent alarmIntent = new Intent(AlarmReceiver.UPDATE_FILTER_ACTION);
            boolean isRunning = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null;
            if (!isRunning) {
                LOG.info("Starting scheduler of filters updating");
                PendingIntent broadcastIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, UPDATE_INITIAL_PERIOD, UPDATE_INTERVAL_PERIOD, broadcastIntent);
                }
            } else {
                LOG.info("Filters update is running");
            }
        }
    }

    @Override
    public void tryUpdateFilters() {
        FilterService filterService = ServiceLocator.getInstance(context).getFilterService();
        PreferencesService preferencesService = ServiceLocator.getInstance(context).getPreferencesService();

        List<FilterList> filterLists = filterService.checkFilterUpdates(false);
        if (!CollectionUtils.isEmpty(filterLists)) {
            filterService.applyNewSettings();
        }

        preferencesService.setLastUpdateCheck(System.currentTimeMillis());
    }

    @Override
    public void updateFilterEnabled(FilterList filter, boolean enabled) {
        filter.setEnabled(enabled);
        filterListDao.updateFilterEnabled(filter, enabled);
    }

    @Override
    public void importUserRulesFromUrl(Activity activity, String url) {
        LOG.info("Start import user rules from {}", url);

        ProgressDialog progressDialog = showProgressDialog(activity, R.string.importUserRulesProgressDialogTitle, R.string.importUserRulesProgressDialogMessage);
        DispatcherThreadPool.getInstance().submit(new ImportUserRulesTask(activity, progressDialog, url));
        LOG.info("Submitted import user rules task");
    }

    @Override
    public List<String> getAllEnabledRules() {
        List<Integer> filterIds = getEnabledFilterIds();
        return filterRuleDao.selectRuleTexts(filterIds, true);
    }

    @Override
    public List<Integer> getEnabledFilterIds() {
        List<Integer> filterIds = new ArrayList<>();
        for (FilterList filter : getEnabledFilters()) {
            filterIds.add(filter.getFilterId());
        }
        return filterIds;
    }

    @Override
    public boolean isShowUsefulAds() {
        final FilterList filter = filterListDao.selectFilterList(SHOW_USEFUL_ADS_FILTER_ID);
        return filter != null && filter.isEnabled();
    }

    @Override
    public void setShowUsefulAds(boolean value) {
        final FilterList filter = filterListDao.selectFilterList(SHOW_USEFUL_ADS_FILTER_ID);
        if (filter != null) {
            updateFilterEnabled(filter, value);
        }
    }

    @Override
    public String getUserRules() {
        return preferencesService.getUserRules();
    }

    @Override
    public List<String> getUserRulesItems() {
        String[] userRules = StringUtils.split(getUserRules(), "\n");

        ArrayList<String> result = new ArrayList<>();
        for (String rule : userRules) {
            rule = StringUtils.trim(rule);
            if (StringUtils.isNotBlank(rule)) {
                result.add(rule);
            }
        }

        return result;
    }

    @Override
    public void addUserRuleItem(String ruleText) {
        String userRules = getUserRules();
        if (userRules.isEmpty()) {
            userRules = ruleText;
        } else {
            userRules += "\n" + ruleText;
        }

        setUserRules(userRules);
    }

    @Override
    public void setUserRules(String userRules) {
        preferencesService.setUserRuleItems(userRules);
    }

    @Override
    public void clearUserRules() {
        setUserRules(StringUtils.EMPTY);
        preferencesService.setDisabledUserRules(new HashSet<String>());
    }

    @Override
    public Set<String> getDisabledUserRules() {
        return preferencesService.getDisabledUserRules();
    }

    @Override
    public void enableUserRule(String ruleText, boolean enabled) {
        Set<String> disabledRules = preferencesService.getDisabledUserRules();
        if (!enabled) {
            if (disabledRules.add(ruleText)) {
                preferencesService.setDisabledUserRules(disabledRules);
            }
        } else {
            if (disabledRules.remove(ruleText)) {
                preferencesService.setDisabledUserRules(disabledRules);
            }
        }
    }

    @Override
    public void applyNewSettings() {
        setShowUsefulAds(preferencesService.isShowUsefulAds());

        List<String> rules = getAllEnabledRules();
        String[] userRules = StringUtils.split(preferencesService.getUserRules(), "\n");
        Set<String> disabledUserRules = preferencesService.getDisabledUserRules();
        if (!ArrayUtils.isEmpty(userRules)) {
            for (String userRule : userRules) {
                userRule = StringUtils.trim(userRule);
                
                if (validateRuleText(userRule)
                        && !disabledUserRules.contains(userRule)) {
                    rules.add(userRule);
                }
            }
        }

        cachedFilterRuleCount = rules.size();

        try {
            LOG.info("Saving {} filters...", cachedFilterRuleCount);
            FileUtils.writeLines(new File(context.getFilesDir().getAbsolutePath() + "/filters.txt"), rules);
            preferencesService.setFilterRuleCount(cachedFilterRuleCount);
            enableContentBlocker(context);
        } catch (IOException e) {
            LOG.warn("Unable to save filters to file!!!", e);
        }
    }

    @Override
    public void clearCacheAndUpdateFilters(ProgressDialog progressDialog) {
        DispatcherThreadPool.getInstance().submit(new ClearFilterCacheTask(progressDialog));
    }

    /**
     * Checks the rules of non ascii symbols and control symbols
     *
     * @param userRule rule
     *
     * @return true if correct rule or false
     */
    private boolean validateRuleText(String userRule) {
        return StringUtils.isNotBlank(userRule) &&
                userRule.matches(ASCII_SYMBOL) &&
                StringUtils.length(userRule) > MIN_RULE_LENGTH &&
                !StringUtils.startsWith(userRule, COMMENT) &&
                !StringUtils.startsWith(userRule, ADBLOCK_META_START) &&
                !StringUtils.contains(userRule, MASK_OBSOLETE_SCRIPT_INJECTION) &&
                !StringUtils.contains(userRule, MASK_OBSOLETE_STYLE_INJECTION);
    }

    /**
     * Updates filters without updates for some time.
     *
     * @param force If true - updates not only over wifi
     * @return List of updated filters or null if something gone wrong
     */
    private List<FilterList> checkOutdatedFilterUpdates(boolean force) {
        if (!force) {
            if (!NetworkUtils.isNetworkAvailable(context) || !InternetUtils.isInternetAvailable()) {
                LOG.info("checkOutdatedFilterUpdates: internet is not available, doing nothing.");
                return new ArrayList<>();
            }

            if (preferencesService.isUpdateOverWifiOnly() && !NetworkUtils.isConnectionWifi(context)) {
                LOG.info("checkOutdatedFilterUpdates: Updates permitted over Wi-Fi only, doing nothing.");
                return new ArrayList<>();
            }

            boolean updateFilters = preferencesService.isAutoUpdateFilters();
            if (!updateFilters) {
                LOG.info("Filters auto-update is disabled, doing nothing");
                return new ArrayList<>();
            }
        }

        List<FilterList> filtersToUpdate = new ArrayList<>();
        long timeFromUpdate = System.currentTimeMillis() - UPDATE_INVALIDATE_PERIOD;
        for (FilterList filter : getEnabledFilters()) {

            if (force || (filter.getLastTimeDownloaded() == null) || (filter.getLastTimeDownloaded().getTime() - timeFromUpdate < 0)) {
                filtersToUpdate.add(filter);
            }
        }

        return checkFilterUpdates(filtersToUpdate, force);
    }

    @SuppressLint("UseSparseArrays")
    private List<FilterList> checkFilterUpdates(List<FilterList> filters, boolean force) {
        LOG.info("Start checking filters updates for {} outdated filters. Forced={}", filters.size(), force);

        if (CollectionUtils.isEmpty(filters)) {
            LOG.info("Empty filters list, doing nothing");
            return new ArrayList<>();
        }

        preferencesService.setLastUpdateCheck(new Date().getTime());

        try {
            final List<FilterList> updated = ServiceApiClient.downloadFilterVersions(context, filters);
            if (updated == null) {
                LOG.warn("Cannot download filter updates.");
                return null;
            }

            Map<Integer, FilterList> map = new HashMap<>();
            for (FilterList filter : updated) {
                map.put(filter.getFilterId(), filter);
            }

            for (FilterList current : filters) {
                final int filterId = current.getFilterId();
                if (!map.containsKey(filterId)) {
                    current.setLastTimeDownloaded(new Date());
                    updateFilter(current);
                    continue;
                }

                FilterList update = map.get(filterId);
                if (update.getVersion().compareTo(current.getVersion()) > 0) {
                    current.setVersion(update.getVersion().toString());
                    current.setLastTimeDownloaded(new Date());
                    current.setTimeUpdated(update.getTimeUpdated());
                    map.put(filterId, current);

                    LOG.info("Updating filter:" + current.getFilterId());
                    updateFilter(current);
                    LOG.info("Updating rules for filter:" + current.getFilterId());
                    updateFilterRules(filterId);
                } else {
                    map.remove(filterId);
                    current.setLastTimeDownloaded(new Date());
                    updateFilter(current);
                }
            }

            LOG.info("Finished checking filters updates.");

            return new ArrayList<>(map.values());
        } catch (IOException e) {
            LOG.error("Error checking filter updates:\r\n", e);
        } catch (Exception e) {
            LOG.error("Error parsing server response:\r\n", e);
        }

        return null;
    }

    private List<FilterList> getEnabledFilters() {
        List<FilterList> enabledFilters = new ArrayList<>();

        List<FilterList> filters = getFilters();
        for (FilterList filter : filters) {
            if (filter.isEnabled()) {
                enabledFilters.add(filter);
            }
        }

        LOG.info("Found {} enabled filters", enabledFilters.size());

        return enabledFilters;
    }

    private void updateFilterRules(int filterId) throws IOException {
        final List<String> rules = ServiceApiClient.downloadFilterRules(context, filterId);
        filterRuleDao.setFilterRules(filterId, rules);
    }

    private void updateFilter(FilterList current) {
        filterListDao.updateFilter(current);
    }

    private boolean isJobCreated(JobScheduler jobScheduler, final int jobId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            return jobScheduler.getPendingJob(jobId) != null;
        }

        JobInfo jobInfo = IterableUtils.find(jobScheduler.getAllPendingJobs(), new Predicate<JobInfo>() {
            @Override
            public boolean evaluate(JobInfo jobInfo) {
                return jobInfo != null && jobId == jobInfo.getId();
            }
        });

        return jobInfo != null;
    }

    /**
     * Task for importing user rules
     */
    private class ImportUserRulesTask extends LongRunningTask {

        private Activity activity;
        private String url;

        private OnImportListener onImportListener;

        ImportUserRulesTask(Activity activity, ProgressDialog progressDialog, String url) {
            super(progressDialog);
            this.activity = activity;
            this.url = url;

            if (activity instanceof OnImportListener) {
                onImportListener = (OnImportListener) activity;
            }
        }

        @Override
        protected void processTask() throws Exception {
            LOG.info("Downloading user rules from {}", url);
            if (url.startsWith("content://")) {
                InputStream inputStream = null;
                try {
                    ContentResolver contentResolver = activity.getContentResolver();
                    inputStream = contentResolver.openInputStream(Uri.parse(url));
                    String buf = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    importRules(buf);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
                return;
            }

            String file = loadFromFile(url);
            final String download = file != null ? file : UrlUtils.downloadString(url);
            importRules(download);
        }

        private void importRules(String download) {
            final String[] rules = StringUtils.split(download, "\n");

            if (rules == null || rules.length < 1) {
                LOG.error("Error downloading user rules from {}", url);
                onError();
                return;
            }

            LOG.info("{} user rules downloaded from {}", rules.length);

            final List<String> rulesList = new ArrayList<>(rules.length);
            for (String rule : rules) {
                final String trimmedRule = rule.trim();
                if (StringUtils.isNotBlank(trimmedRule) && trimmedRule.length() < 8000) {
                    rulesList.add(trimmedRule);
                }
            }

            if (rulesList.size() < 1) {
                LOG.error("Invalid user rules from {}", url);
                onError();
                return;
            }

            preferencesService.setUserRuleItems(StringUtils.join(rulesList, "\n"));
            LOG.info("User rules added successfully.");

            ServiceLocator.getInstance(activity.getApplicationContext()).getFilterService().applyNewSettings();

            String message = activity.getString(R.string.importUserRulesSuccessResultMessage).replace("{0}", String.valueOf(rulesList.size()));
            showToast(activity, message);

            if (onImportListener != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onImportListener.onSuccess();
                    }
                });
            }
        }

        private void onError() {
            String message = activity.getString(R.string.importUserRulesErrorResultMessage);
            showToast(activity, message);
        }

        private String loadFromFile(String url) throws Exception {
            File f = new File(url);
            if (f.exists() && f.isFile() && f.canRead()) {
                FileInputStream fis = new FileInputStream(f);
                byte[] buf = IoUtils.readToEnd(fis);
                IOUtils.closeQuietly(fis);
                return new String(buf);
            }
            return null;
        }
    }

    public interface OnImportListener {
        void onSuccess();
    }

    /**
     * Task for checking updates
     */
    private class CheckUpdatesTask extends LongRunningTask {

        private Activity activity;

        CheckUpdatesTask(Activity activity, ProgressDialog progressDialog) {
            super(progressDialog);
            this.activity = activity;
        }

        @Override
        protected void processTask() throws Exception {
            final List<FilterList> filters = ServiceLocator.getInstance(context).getFilterService().checkFilterUpdates(true);
            if (filters == null) {
                String message = activity.getString(R.string.checkUpdatesErrorResultMessage);
                showToast(activity, message);
            } else {
                if (filters.size() == 0) {
                    String message = activity.getString(R.string.checkUpdatesZeroResultMessage);
                    showToast(activity, message);
                } else {
                    if (filters.size() == 1) {
                        String message = activity.getString(R.string.checkUpdatesOneResultMessage).replace("{0}", parseFilterNames(filters));
                        showToast(activity, message);
                    } else {
                        String message = activity.getString(R.string.checkUpdatesManyResultMessage)
                                .replace("{0}", Integer.toString(filters.size()))
                                .replace("{1}", parseFilterNames(filters));
                        showToast(activity, message);
                    }
                }
                preferencesService.setLastUpdateCheck(System.currentTimeMillis());
            }
            applyNewSettings();
        }

        private String parseFilterNames(List<FilterList> filters) {
            StringBuilder sb = new StringBuilder();
            for (FilterList filter : filters) {
                sb.append(" ");
                sb.append(filter.getName());
                sb.append(",");
            }

            if (sb.indexOf(",") > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }

            return sb.toString();
        }
    }

    private class ClearFilterCacheTask extends LongRunningTask {

        ClearFilterCacheTask(ProgressDialog progressDialog) {
            super(progressDialog);
        }

        @Override
        protected void processTask() {
            String[] fileList = context.fileList();

            for (String file : fileList) {
                if (StringUtils.startsWith(file, "filter_")) {
                    context.deleteFile(file);
                }
            }

            checkFilterUpdates(true);
        }
    }
}
