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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.adguard.android.contentblocker.preferences.PreferenceDb;
import com.adguard.commons.enums.FilteringQuality;
import com.adguard.filter.WorkaroundUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Application preferences helper.
 * Preferences keys should be associated with /res/xml/preferences.xml.
 */
@SuppressLint("CommitPrefEdits")
public class PreferencesServiceImpl implements PreferencesService {

    private final static Logger LOG = LoggerFactory.getLogger(PreferencesServiceImpl.class);

    private final static String KEY_AUTOUPDATE_FILTERS = "auto_update_filters";
    private final static String KEY_UPDATE_OVER_WIFI = "update_over_wifi";
    private final static String KEY_SEND_ANONYMOUS_STATISTICS = "help_browsing_security_enabled";
    private final static String KEY_LAST_VERSION_FOUND = "last_version_found";
    private final static String KEY_LAST_UPDATE_CHECK_DATE = "last_update_check_date";
    private final static String KEY_LOG_LEVEL = "log_level";
    private final static String KEY_REFERRER = "referrer";
    private final static String KEY_APP_LANGUAGE = "app_language";
    private final static String KEY_WHITELIST = "whitelist";
    private final static String KEY_USER_RULES = "user_rules";
    private final static String KEY_FILTERING_QUALITY = "filtering_quality";
    private final static String KEY_DEVICE_SPEED_RANK = "device_speed_rank";
    private final static String KEY_FIRST_START_TUTORIAL = "key_first_start_tutorial";
    private final static String KEY_LAST_IMPORT_URL = "key_last_import_rule";
    private final static String KEY_UPDATE_CHANNEL = "key_update_channel";
    private final static String KEY_FILTER_RULE_COUNT = "key_filter_rule_count";

    private final SharedPreferences sharedPreferences;
    private final Context context;

    /**
     * Creates an instance of PreferencesService
     *
     * @param context Context
     */
    public PreferencesServiceImpl(Context context) {
        LOG.info("Creating PreferencesService instance for {}", context);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    /**
     * @return true if autoupdate filters is enabled
     */
    @Override
    public boolean isAutoUpdateFilters() {
        return sharedPreferences.getBoolean(KEY_AUTOUPDATE_FILTERS, true);
    }

    @Override
    public void setAutoUpdateFilters(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_AUTOUPDATE_FILTERS, value);
        editor.commit();
    }

    @Override
    public boolean isUpdateOverWifiOnly() {
        return sharedPreferences.getBoolean(KEY_UPDATE_OVER_WIFI, false);
    }

    @Override
    public void setUpdateOverWifiOnly(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_UPDATE_OVER_WIFI, value);
        editor.commit();
    }

    @Override
    public boolean isSendAnonymousStatistics() {
        return sharedPreferences.getBoolean(KEY_SEND_ANONYMOUS_STATISTICS, true);
    }

    @Override
    public void setSendAnonymousStatistics(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_SEND_ANONYMOUS_STATISTICS, value);
        editor.commit();
    }

    @Override
    public Set<String> getWhiteList() {
        return sharedPreferences.getStringSet(KEY_WHITELIST, new HashSet<String>());
    }

    /**
     * Adds item to whitelist
     *
     * @param item Item to add
     */
    @Override
    public void addToWhitelist(String item) {
        final Set<String> stringSet = sharedPreferences.getStringSet(KEY_WHITELIST, new HashSet<String>());
        stringSet.add(item);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_WHITELIST);
        editor.putStringSet(KEY_WHITELIST, stringSet);
        editor.commit();
    }

    /**
     * Clears whitelist
     */
    @Override
    public void clearWhiteList() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_WHITELIST);
        editor.commit();
    }

    /**
     * Removes whitelist item
     *
     * @param item Item to remove
     */
    @Override
    public void removeWhiteListItem(String item) {
        final Set<String> stringSet = sharedPreferences.getStringSet(KEY_WHITELIST, new HashSet<String>());
        stringSet.remove(item);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_WHITELIST);
        editor.putStringSet(KEY_WHITELIST, stringSet);
        editor.commit();
    }

    /**
     * User rules list
     *
     * @return List of user rules
     */
    @Override
    public Set<String> getUserRules() {
        return sharedPreferences.getStringSet(KEY_USER_RULES, new HashSet<String>());
    }

    /**
     * Adds user rule
     *
     * @param item User rule
     */
    @Override
    public void addUserRuleItem(String item) {
        final Set<String> stringSet = sharedPreferences.getStringSet(KEY_USER_RULES, new HashSet<String>());
        stringSet.add(item);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_RULES);
        editor.putStringSet(KEY_USER_RULES, stringSet);
        editor.commit();
    }

    /**
     * Removes user rule
     *
     * @param item Item to remove
     */
    @Override
    public void removeUserRuleItem(String item) {
        final Set<String> stringSet = sharedPreferences.getStringSet(KEY_USER_RULES, new HashSet<String>());
        stringSet.remove(item);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_RULES);
        editor.putStringSet(KEY_USER_RULES, stringSet);
        editor.commit();
    }

    /**
     * Clears user rules list
     */
    @Override
    public void clearUserRules() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_RULES);
        editor.commit();
    }

    @Override
    public void addUserRuleItems(Collection<String> items) {
        final Set<String> stringSet = sharedPreferences.getStringSet(KEY_USER_RULES, new HashSet<String>());
        stringSet.addAll(items);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_RULES);
        editor.putStringSet(KEY_USER_RULES, stringSet);
        editor.commit();
    }

    @Override
    public String getLastVersionFound() {
        return sharedPreferences.getString(KEY_LAST_VERSION_FOUND, null);
    }

    @Override
    public void setLastVersionFound(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_VERSION_FOUND, value);
        editor.commit();
    }

    @Override
    public void setLastUpdateCheck(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LAST_UPDATE_CHECK_DATE, time);
        editor.commit();
    }

    @Override
    public Date getLastUpdateCheck() {
        final long time = sharedPreferences.getLong(KEY_LAST_UPDATE_CHECK_DATE, 0);
        return time > 0 ? new Date(time) : null;
    }

    @Override
    public void setLogLevel(int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_LOG_LEVEL, value);
        editor.commit();
    }

    @Override
    public int getLogLevel() {
        return sharedPreferences.getInt(KEY_LOG_LEVEL, 0);
    }

    @Override
    public void setReferrer(String referrerString) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_REFERRER, referrerString);
        editor.commit();
    }

    @Override
    public String getReferrer() {
        return sharedPreferences.getString(KEY_REFERRER, null);
    }

    @Override
    public String getAppLanguage() {
        return sharedPreferences.getString(KEY_APP_LANGUAGE, null);
    }

    @Override
    public void setAppLanguage(String languageCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_APP_LANGUAGE, languageCode);
        editor.commit();
    }

    @Override
    public FilteringQuality getFilteringQuality() {
        if (!sharedPreferences.contains(KEY_FILTERING_QUALITY)) {
            int deviceSpeedRank = getDeviceSpeedRank();
            FilteringQuality filteringQuality = WorkaroundUtils.getDefaultFilteringQuality(deviceSpeedRank);
            LOG.info("Initializing Filtering Quality with {}", filteringQuality);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_FILTERING_QUALITY, filteringQuality.getCode());
            editor.commit();
        }

        return FilteringQuality.getByCode(sharedPreferences.getInt(KEY_FILTERING_QUALITY, 0));
    }

    @Override
    public int getDeviceSpeedRank() {
        if (!sharedPreferences.contains(KEY_DEVICE_SPEED_RANK)) {
            int deviceSpeedRank = WorkaroundUtils.calculateDeviceSpeedRank(context);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_DEVICE_SPEED_RANK, deviceSpeedRank);
            editor.commit();
        }

        return sharedPreferences.getInt(KEY_DEVICE_SPEED_RANK, 400);
    }

    @Override
    public void setFilteringQuality(FilteringQuality filteringQuality) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_FILTERING_QUALITY, filteringQuality.getCode());
        editor.commit();
    }

    @Override
    public boolean isFirstStartTutorialNeeded() {
        if (sharedPreferences.getBoolean(KEY_FIRST_START_TUTORIAL, true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_FIRST_START_TUTORIAL, false);
            editor.commit();
            return true;
        }

        return false;
    }

    @Override
    public String getLastImportUrl() {
        return sharedPreferences.getString(KEY_LAST_IMPORT_URL, null);
    }

    @Override
    public void setLastImportUrl(String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_IMPORT_URL, url);
        editor.commit();
    }

    @Override
    public int getUpdateChannel() {
        return sharedPreferences.getInt(KEY_UPDATE_CHANNEL, 0);
    }

    @Override
    public void setUpdateChannel(int selectedChannel) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_UPDATE_CHANNEL, selectedChannel);
        editor.commit();
    }

    @Override
    public boolean isShowUsefulAds() {
        return sharedPreferences.getBoolean(PreferenceDb.PREF_SHOW_USEFUL_ADS, true);
    }

    @Override
    public void setFilterRuleCount(int ruleCount) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_FILTER_RULE_COUNT, ruleCount);
        editor.commit();
    }

    @Override
    public int getFilterRuleCount() {
        return sharedPreferences.getInt(KEY_FILTER_RULE_COUNT, 0);
    }
}
