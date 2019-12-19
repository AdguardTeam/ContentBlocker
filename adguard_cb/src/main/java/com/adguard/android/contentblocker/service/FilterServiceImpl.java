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
package com.adguard.android.contentblocker.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ServiceApiClient;
import com.adguard.android.contentblocker.commons.BrowserUtils;
import com.adguard.android.contentblocker.commons.StringHelperUtils;
import com.adguard.android.contentblocker.commons.TextStatistics;
import com.adguard.android.contentblocker.commons.concurrent.DispatcherThreadPool;
import com.adguard.android.contentblocker.commons.io.IoUtils;
import com.adguard.android.contentblocker.commons.network.NetworkUtils;
import com.adguard.android.contentblocker.db.DbHelper;
import com.adguard.android.contentblocker.db.FilterListDao;
import com.adguard.android.contentblocker.db.FilterListDaoImpl;
import com.adguard.android.contentblocker.db.FilterRuleDao;
import com.adguard.android.contentblocker.db.FilterRuleDaoImpl;
import com.adguard.android.contentblocker.model.FilterList;
import com.adguard.android.contentblocker.ui.utils.ProgressDialogUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.io.ByteOrderMark.UTF_16BE;
import static org.apache.commons.io.ByteOrderMark.UTF_16LE;
import static org.apache.commons.io.ByteOrderMark.UTF_32BE;
import static org.apache.commons.io.ByteOrderMark.UTF_32LE;
import static org.apache.commons.io.ByteOrderMark.UTF_8;

/**
 * Filter service implementation.
 */
public class FilterServiceImpl implements FilterService {
    private static final Logger LOG = LoggerFactory.getLogger(FilterServiceImpl.class);

    private static final int MIN_RULE_LENGTH = 4;
    private static final String ASCII_SYMBOL = "\\p{ASCII}+";
    private static final String COMMENT = "!";
    private static final String ADBLOCK_META_START = "[Adblock";
    private static final String MASK_OBSOLETE_SCRIPT_INJECTION = "###adg_start_script_inject";
    private static final String MASK_OBSOLETE_STYLE_INJECTION = "###adg_start_style_inject";

    private static final int UPDATE_INVALIDATE_PERIOD = 24 * 60 * 60 * 1000; // 24 hours

    private static final String FILTERS_UPDATE_QUEUE = "filters-update-queue";

    private final Context context;
    private final FilterListDao filterListDao;
    private final FilterRuleDao filterRuleDao;
    private final PreferencesService preferencesService;
    private final NotificationService notificationService;

    private int cachedFilterRuleCount = 0;

    /**
     * Creates an instance of AdguardService
     *
     * @param context Context
     */
    public FilterServiceImpl(Context context, DbHelper dbHelper, PreferencesService preferencesService, NotificationService notificationService) {
        LOG.info("Creating AdguardService instance for {}", context);
        this.context = context;
        filterListDao = new FilterListDaoImpl(context, dbHelper);
        filterRuleDao = new FilterRuleDaoImpl(context);

        this.preferencesService = preferencesService;
        this.notificationService = notificationService;
    }

    @Override
    public void checkFiltersUpdates(Activity activity) {
        LOG.info("Start manual filters updates check");
        preferencesService.setLastUpdateCheck(new Date().getTime());

        ProgressDialog progressDialog = ProgressDialogUtils.showProgressDialog(activity, R.string.checkUpdatesProgressDialogTitle, R.string.checkUpdatesProgressDialogMessage);
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
    public void enableContentBlocker(Context context) {
        Set<String> browsers = BrowserUtils.getKnownBrowsers();

        for (String browser : browsers) {
            sendUpdateFiltersInBrowser(context, browser);
        }

        sendUpdateFiltersInBrowser(context, null);
    }

    @Override
    public boolean tryUpdateFilters() {
        List<FilterList> filterLists = checkFilterUpdates(false);
        if (filterLists == null) {
            return false;
        }

        if (!CollectionUtils.isEmpty(filterLists)) {
            applyNewSettings();
        }
        preferencesService.setLastUpdateCheck(System.currentTimeMillis());
        return true;
    }

    @Override
    public void updateFilterEnabled(FilterList filter, boolean enabled) {
        filter.setEnabled(enabled);
        filterListDao.updateFilterEnabled(filter, enabled);
    }

    @Override
    public void importUserRulesFromUrl(Activity activity, String url, boolean overwrite) {
        LOG.info("Start import user rules from {}", url);

        ProgressDialog progressDialog = ProgressDialogUtils.showProgressDialog(activity, R.string.importUserRulesProgressDialogTitle, R.string.importUserRulesProgressDialogMessage);
        DispatcherThreadPool.getInstance().submit(new ImportUserRulesTask(activity, progressDialog, url, overwrite));
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
        return StringHelperUtils.splitAndTrim(getUserRules(), "\n");
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
        preferencesService.setDisabledUserRules(new HashSet<>());
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
    public String getWhiteList() {
        return preferencesService.getWhitelist();
    }

    @Override
    public List<String> getWhiteListItems() {
        return StringHelperUtils.splitAndTrim(getWhiteList(), "\n");
    }

    @Override
    public void addWhitelistItem(String item) {
        String whiteList = getWhiteList();
        if (StringUtils.isBlank(whiteList)) {
            whiteList = item;
        } else {
            whiteList += "\n" + item;
        }
        setWhiteList(whiteList);
    }

    @Override
    public void setWhiteList(String whitelist) {
        preferencesService.setWhitelist(whitelist);
    }

    @Override
    public void clearWhiteList() {
        setWhiteList(StringUtils.EMPTY);
        preferencesService.setDisabledWhitelistRules(new HashSet<>());
    }

    @Override
    public Set<String> getDisabledWhitelistRules() {
        return preferencesService.getDisabledWhitelistRules();
    }

    @Override
    public void enableWhitelistRule(String ruleText, boolean enabled) {
        Set<String> disabledRules = preferencesService.getDisabledWhitelistRules();
        if (!enabled) {
            if (disabledRules.add(ruleText)) {
                preferencesService.setDisabledWhitelistRules(disabledRules);
            }
        } else {
            if (disabledRules.remove(ruleText)) {
                preferencesService.setDisabledWhitelistRules(disabledRules);
            }
        }
    }

    @Override
    public void applyNewSettings() {
        List<String> rules = getAllEnabledRules();

        List<String> userRules = StringHelperUtils.splitAndTrim(preferencesService.getUserRules(), "\n");
        Set<String> disabledUserRules = preferencesService.getDisabledUserRules();
        for (String userRule : userRules) {
            if (validateRuleText(userRule) && !disabledUserRules.contains(userRule)) {
                rules.add(userRule);
            }
        }

        List<String> whitelistRules = StringHelperUtils.splitAndTrim(preferencesService.getWhitelist(), "\n");
        Set<String> disabledWhitelistRules = preferencesService.getDisabledWhitelistRules();
        for (String whitelistRule : whitelistRules) {
            if (!disabledWhitelistRules.contains(whitelistRule)) {
                rules.add(createWhiteListRule(whitelistRule));

                /**
                 * Add these rules, because the Ya Browser does not support the $document modifier
                 */
                // TODO Should remove this after the Ya Browser browser add support $document modifier
                rules.add(String.format("@@http*$domain=%s", whitelistRule));
                rules.add(String.format("@@||%s^$elemhide", whitelistRule));
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

    private void sendUpdateFiltersInBrowser(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_UPDATE");
        intent.setData(Uri.parse("package:com.adguard.android.contentblocker"));
        intent.setPackage(packageName);
        context.sendBroadcast(intent);
    }

    /**
     * Creates whilelist rule from domain name
     *
     * @param domain Domain name
     * @return Url filter rule text
     */
    private String createWhiteListRule(String domain) {
        return "@@{0}^$document".replace("{0}", domain);
    }

    /**
     * Checks the rules of non ascii symbols and control symbols
     *
     * @param userRule rule
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
            boolean updateFilters = preferencesService.isAutoUpdateFilters();
            if (!updateFilters) {
                LOG.info("Filters auto-update is disabled, doing nothing");
                return null;
            }

            if (preferencesService.isUpdateOverWifiOnly() && !NetworkUtils.isConnectionWifi(context)) {
                LOG.info("checkOutdatedFilterUpdates: Updates permitted over Wi-Fi only, doing nothing.");
                return null;
            }
        }

        List<FilterList> filtersToUpdate = new ArrayList<>();
        long timeFromUpdate = System.currentTimeMillis() - UPDATE_INVALIDATE_PERIOD;
        for (FilterList filter : getEnabledFilters()) {

            if (force || shouldUpdateOutdatedFilter(filter, timeFromUpdate)) {
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
            final List<FilterList> updated = ServiceApiClient.downloadFilterVersions(filters);
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
                if (update.getVersion().compareTo(current.getVersion()) > 0
                        || !filterRuleDao.hasFilterRules(filterId)) {

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
        final List<String> rules = ServiceApiClient.downloadFilterRules(filterId);
        filterRuleDao.setFilterRules(filterId, rules);
    }

    private void updateFilter(FilterList current) {
        filterListDao.updateFilter(current);
    }

    private boolean shouldUpdateOutdatedFilter(FilterList filterList, long timeFromUpdate) {
        if (!filterList.isEnabled()) {
            return false;
        }

        Date lastTimeDownloaded = filterList.getLastTimeDownloaded();
        return lastTimeDownloaded == null || (lastTimeDownloaded.getTime() - timeFromUpdate < 0);
    }

    /**
     * Task for importing user rules
     */
    private class ImportUserRulesTask extends LongRunningTask {

        private final Activity activity;
        private final String url;
        private final boolean overwrite;

        private OnImportListener onImportListener;

        ImportUserRulesTask(Activity activity, ProgressDialog progressDialog, String url, boolean overwrite) {
            super(progressDialog);
            this.activity = activity;
            this.url = url;
            this.overwrite = overwrite;

            if (activity instanceof OnImportListener) {
                onImportListener = (OnImportListener) activity;
            }
        }

        @Override
        protected void processTask() {
            LOG.info("Downloading user rules from {}", url);
            InputStream inputStream = null;
            BOMInputStream bomInputStream = null;
            try {
                inputStream = IoUtils.getInputStreamFromUrl(context, url);
                if (isTextPlain(inputStream)) {
                    bomInputStream = new BOMInputStream(inputStream, UTF_8, UTF_16BE, UTF_16LE, UTF_32BE, UTF_32LE);
                    importRules(IOUtils.toString(bomInputStream, "utf-8"));
                } else {
                    notificationService.showToast(R.string.importUserRulesErrorResultMessage);
                }
            } catch (IOException e) {
                LOG.error("Error downloading user rules from {}", url, e);
                notificationService.showToast(R.string.importUserRulesErrorResultMessage);
            } finally {
                IoUtils.closeQuietly(bomInputStream);
                IoUtils.closeQuietly(inputStream);
            }
        }

        /**
         * Checks that the input stream contains text
         *
         * @param inputStream Input stream
         * @return true if input stream contains the text otherwise false
         */
        private boolean isTextPlain(InputStream inputStream) throws IOException {
            if (inputStream == null) {
                return false;
            }

            try {
                byte[] buffer = new byte[512];
                inputStream.mark(buffer.length);
                int read = inputStream.read(buffer);
                if (read != -1) {
                    TextStatistics textStatistics = new TextStatistics();
                    textStatistics.addData(buffer, 0, read);

                    return textStatistics.isMostlyAscii() || textStatistics.looksLikeUTF8();
                }
            } finally {
                inputStream.reset();
            }

            return false;
        }


        private void importRules(String download) {
            final String[] rules = StringUtils.split(download, "\n");

            if (rules == null || rules.length < 1) {
                LOG.error("Error downloading user rules from {}", url);
                onError();
                return;
            }

            LOG.info("{} user rules downloaded from {}", rules.length, url);

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

            String newRules = StringUtils.join(rulesList, "\n");
            if (!overwrite) {
                newRules = preferencesService.getUserRules() + "\n" + newRules;
            }

            preferencesService.setUserRuleItems(newRules);
            LOG.info("User rules added successfully.");

            applyNewSettings();

            String message = activity.getString(R.string.importUserRulesSuccessResultMessage).replace("{0}", String.valueOf(rulesList.size()));
            notificationService.showToast(message);

            if (onImportListener != null) {
                activity.runOnUiThread(() -> onImportListener.onSuccess());
            }
        }

        private void onError() {
            String message = activity.getString(R.string.importUserRulesErrorResultMessage);
            notificationService.showToast(message);
        }
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
        protected void processTask() {
            final List<FilterList> filters = checkFilterUpdates(true);
            if (filters == null) {
                String message = activity.getString(R.string.checkUpdatesErrorResultMessage);
                notificationService.showToast(message);
                return;
            }

            if (filters.size() == 0) {
                String message = activity.getString(R.string.checkUpdatesZeroResultMessage);
                notificationService.showToast(message);
            } else if (filters.size() == 1) {
                String message = activity.getString(R.string.checkUpdatesOneResultMessage).replace("{0}", parseFilterNames(filters));
                notificationService.showToast(message);
            } else {
                String message = activity.getString(R.string.checkUpdatesManyResultMessage)
                        .replace("{0}", Integer.toString(filters.size()))
                        .replace("{1}", parseFilterNames(filters));
                notificationService.showToast(message);
            }
            preferencesService.setLastUpdateCheck(System.currentTimeMillis());

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
            applyNewSettings();
        }
    }
}
