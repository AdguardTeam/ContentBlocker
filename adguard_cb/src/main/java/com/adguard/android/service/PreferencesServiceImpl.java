/*
 This file is part of Adguard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2018 Adguard Software Ltd. All rights reserved.

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
@SuppressLint("applyPrefEdits")
public class PreferencesServiceImpl implements PreferencesService {

    private final static Logger LOG = LoggerFactory.getLogger(PreferencesServiceImpl.class);

    private static final String PREF_SHOW_USEFUL_ADS = "pref_show_useful_ads";
    private final static String KEY_AUTOUPDATE_FILTERS = "auto_update_filters";
    private final static String KEY_UPDATE_OVER_WIFI = "update_over_wifi";
    private final static String KEY_LAST_UPDATE_CHECK_DATE = "last_update_check_date";
    private final static String KEY_WHITELIST = "whitelist";
    private final static String KEY_USER_RULES = "user_rules";
    private final static String KEY_LAST_IMPORT_URL = "key_last_import_rule";
    private final static String KEY_FILTER_RULE_COUNT = "key_filter_rule_count";
    private final static String KEY_BROWSER_CONNECTED_COUNT = "key_browser_connected_count";
    private final static String KEY_ONBOARDING_SHOWN = "key_onboarding_shown";
    private final static String KEY_ABOUT_OTHER_PRODUCT_SHOWN = "key_about_other_product_shown";

    private final SharedPreferences sharedPreferences;

    /**
     * Creates an instance of PreferencesService
     *
     * @param context Context
     */
    public PreferencesServiceImpl(Context context) {
        LOG.info("Creating PreferencesService instance for {}", context);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        editor.apply();
    }

    @Override
    public boolean isUpdateOverWifiOnly() {
        return sharedPreferences.getBoolean(KEY_UPDATE_OVER_WIFI, false);
    }

    @Override
    public void setUpdateOverWifiOnly(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_UPDATE_OVER_WIFI, value);
        editor.apply();
    }

    @Override
    public boolean isOnboardingShown() {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_SHOWN, false);
    }

    @Override
    public void setOnboardingShown(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ONBOARDING_SHOWN, value);
        editor.apply();
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
        editor.apply();
    }

    /**
     * Clears whitelist
     */
    @Override
    public void clearWhiteList() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_WHITELIST);
        editor.apply();
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
        editor.apply();
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
        editor.apply();
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
        editor.apply();
    }

    /**
     * Clears user rules list
     */
    @Override
    public void clearUserRules() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_RULES);
        editor.apply();
    }

    @Override
    public void addUserRuleItems(Collection<String> items) {
        final Set<String> stringSet = sharedPreferences.getStringSet(KEY_USER_RULES, new HashSet<String>());
        stringSet.addAll(items);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_RULES);
        editor.putStringSet(KEY_USER_RULES, stringSet);
        editor.apply();
    }

    @Override
    public void setLastUpdateCheck(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LAST_UPDATE_CHECK_DATE, time);
        editor.apply();
    }

    @Override
    public Date getLastUpdateCheck() {
        final long time = sharedPreferences.getLong(KEY_LAST_UPDATE_CHECK_DATE, 0);
        return time > 0 ? new Date(time) : null;
    }

    @Override
    public String getLastImportUrl() {
        return sharedPreferences.getString(KEY_LAST_IMPORT_URL, null);
    }

    @Override
    public void setLastImportUrl(String url) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_IMPORT_URL, url);
        editor.apply();
    }

    @Override
    public void setShowUsefulAds(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_SHOW_USEFUL_ADS, value);
        editor.apply();
    }

    @Override
    public boolean isShowUsefulAds() {
        return sharedPreferences.getBoolean(PREF_SHOW_USEFUL_ADS, true);
    }

    @Override
    public void setFilterRuleCount(int ruleCount) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_FILTER_RULE_COUNT, ruleCount);
        editor.apply();
    }

    @Override
    public int getFilterRuleCount() {
        return sharedPreferences.getInt(KEY_FILTER_RULE_COUNT, 0);
    }

    public void incBrowserConnectedCount() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_BROWSER_CONNECTED_COUNT, sharedPreferences.getInt(KEY_BROWSER_CONNECTED_COUNT, 0) + 1);
        editor.apply();
    }

    public int getBrowserConnectedCount() {
        return sharedPreferences.getInt(KEY_BROWSER_CONNECTED_COUNT, 0);
    }

    @Override
    public boolean isWelcomeMessage() {
        return sharedPreferences.getBoolean(KEY_ABOUT_OTHER_PRODUCT_SHOWN, false);
    }

    @Override
    public void setWelcomeMessage(boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ABOUT_OTHER_PRODUCT_SHOWN, value);
        editor.apply();
    }
}
