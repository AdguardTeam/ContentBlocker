/**
 This file is part of Adguard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2016 Performix LLC. All rights reserved.

 Adguard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 Adguard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 Adguard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.adguard.android.contentblocker.AlarmReceiver;
import com.adguard.android.contentblocker.R;
import com.adguard.android.ServiceLocator;
import com.adguard.android.db.FilterListDao;
import com.adguard.android.db.FilterListDaoImpl;
import com.adguard.android.db.FilterRuleDao;
import com.adguard.android.db.FilterRuleDaoImpl;
import com.adguard.android.contentblocker.ServiceApiClient;
import com.adguard.android.model.FilterList;
import com.adguard.commons.NetworkUtils;
import com.adguard.commons.concurrent.DispatcherThreadPool;
import com.adguard.commons.io.IoUtils;
import com.adguard.commons.InternetUtils;
import com.adguard.commons.web.UrlUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

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

    private static final int SOCIAL_MEDIA_WIDGETS_FILTER_ID = 4;
    private static final int SPYWARE_FILTER_ID = 3;

    private static final int UPDATE_INVALIDATE_PERIOD = 24 * 60 * 60 * 1000; // 24 hours
    private static final int UPDATE_INITIAL_PERIOD = 5 * 60 * 1000; // 5 minutes

    private static final String FILTERS_UPDATE_QUEUE = "filters-update-queue";

    private final Context context;
    private final FilterListDao filterListDao;
    private final FilterRuleDao filterRuleDao;
    private final PreferencesService preferencesService;

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
        Intent alarmIntent = new Intent(AlarmReceiver.UPDATE_FILTER_ACTION);

        boolean isRunning = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null;
        if (!isRunning) {
            LOG.info("Starting scheduler of filters updating");
            PendingIntent broadcastIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, UPDATE_INITIAL_PERIOD, AlarmManager.INTERVAL_HOUR, broadcastIntent);
        } else {
            LOG.info("Filters update is running");
        }
    }

    @Override
    public void updateFilterEnabled(FilterList filter, boolean enabled) {
        filter.setEnabled(enabled);
        filterListDao.updateFilterEnabled(filter, enabled);
    }

    @Override
    public Set<String> getWhiteList() {
        return preferencesService.getWhiteList();
    }

    @Override
    public void addToWhitelist(String item) {
        preferencesService.addToWhitelist(item);
    }

    @Override
    public void clearWhiteList() {
        preferencesService.clearWhiteList();
    }

    @Override
    public void removeWhiteListItem(String item) {
        preferencesService.removeWhiteListItem(item);
    }

    @Override
    public Set<String> getUserRules() {
        return preferencesService.getUserRules();
    }

    @Override
    public void addUserRuleItem(String item) {
        preferencesService.addUserRuleItem(item);
    }

    @Override
    public void removeUserRuleItem(String item) {
        preferencesService.removeUserRuleItem(item);
    }

    @Override
    public void clearUserRules() {
        preferencesService.clearUserRules();
    }

    @Override
    public void importUserRulesFromUrl(Activity activity, String url) {
        LOG.info("Start import user rules from {}", url);

        ProgressDialog progressDialog = showProgressDialog(activity, R.string.importUserRulesProgressDialogTitle, R.string.importUserRulesProgressDialogMessage);
        DispatcherThreadPool.getInstance().submit(new ImportUserRulesTask(activity, progressDialog, url));
        LOG.info("Submitted import user rules task");
    }

    @Override
    public List<String> getAllEnabledRules(boolean useCosmetics) {
        List<Integer> filterIds = getEnabledFilterIds();
        return filterRuleDao.selectRuleTexts(filterIds, useCosmetics);
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
    public boolean isSocialMediaWidgetsFilterEnabled() {
        final FilterList filter = filterListDao.selectFilterList(SOCIAL_MEDIA_WIDGETS_FILTER_ID);
        return filter != null && filter.isEnabled();
    }

    @Override
    public void setSocialMediaWidgetsFilterEnabled(boolean value) {
        final FilterList filter = filterListDao.selectFilterList(SOCIAL_MEDIA_WIDGETS_FILTER_ID);
        if (filter != null) {
            updateFilterEnabled(filter, value);
        }
    }

    @Override
    public boolean isSpywareFilterEnabled() {
        final FilterList filter = filterListDao.selectFilterList(SPYWARE_FILTER_ID);
        return filter != null && filter.isEnabled();
    }

    @Override
    public void applyNewSettings() {
        setShowUsefulAds(preferencesService.isShowUsefulAds());

        List<String> rules = getAllEnabledRules(true);
        Set<String> userRules = getUserRules();
        if (!userRules.isEmpty()) {
            for (String userRule : userRules) {
                userRule = StringUtils.trim(userRule);
                
                if (validateRuleText(userRule)) {
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
    public void setSpywareFilterEnabled(boolean value) {
        final FilterList filter = filterListDao.selectFilterList(SPYWARE_FILTER_ID);
        if (filter != null) {
            updateFilterEnabled(filter, value);
        }
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

    /**
     * Task for importing user rules
     */
    private class ImportUserRulesTask extends LongRunningTask {

        private Activity activity;
        private String url;

        private OnImportListener onImportListener;

        public ImportUserRulesTask(Activity activity, ProgressDialog progressDialog, String url) {
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
                    String buf = org.apache.commons.io.IOUtils.toString(inputStream);
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

            preferencesService.addUserRuleItems(rulesList);
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

        public CheckUpdatesTask(Activity activity, ProgressDialog progressDialog) {
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
}
