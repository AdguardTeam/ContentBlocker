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
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public String getUserRules() {
        return sharedPreferences.getString(KEY_USER_RULES_STRING, StringUtils.EMPTY);
    }

    @Override
    public void setUserRuleItems(String userRules) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_RULES_STRING, userRules);
        editor.apply();
    }

    @Override
    public Set<String> getDisabledUserRules() {
        Set<String> valueSet = sharedPreferences.getStringSet(KEY_DISABLED_USER_RULES, null);
        return valueSet != null ? new HashSet<>(valueSet) : new HashSet<String>();
    }

    @Override
    public void setDisabledUserRules(Set<String> disabledUserRules) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_DISABLED_USER_RULES, disabledUserRules);
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
        return sharedPreferences.getBoolean(PREF_SHOW_USEFUL_ADS, false);
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

    @Override
    public Long getLastTimeCommunication() {
        return sharedPreferences.getLong(KEY_LAST_COMMUNICATION_DATE, 0L);
    }

    @Override
    public void setLastTimeCommunication(Long lastTimeCommunication) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_LAST_COMMUNICATION_DATE, lastTimeCommunication);
        editor.apply();
    }

    @Override
    public boolean isAppRated() {
        return sharedPreferences.getBoolean(KEY_APP_RATED, false);
    }

    @Override
    public void setAppRated(boolean appRated) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_APP_RATED, appRated);
        editor.apply();
    }

    @Override
    public String getWhitelist() {
        return sharedPreferences.getString(KEY_WHITELIST_STRING, null);
    }

    @Override
    public void setWhitelist(String whitelist) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_WHITELIST_STRING, whitelist);
        editor.apply();
    }

    @Override
    public Set<String> getDisabledWhitelistRules() {
        return sharedPreferences.getStringSet(KEY_DISABLED_WHITELIST, new HashSet<String>());
    }

    @Override
    public void setDisabledWhitelistRules(Set<String> disabledWhitelistRules) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_DISABLED_WHITELIST, disabledWhitelistRules);
        editor.apply();
    }
}
