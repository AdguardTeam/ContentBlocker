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

import java.util.Date;
import java.util.Set;

/**
 * Service that stores application preferences
 */
public interface PreferencesService {

    String PREF_SHOW_USEFUL_ADS = "pref_show_useful_ads";
    String KEY_AUTOUPDATE_FILTERS = "auto_update_filters";
    String KEY_UPDATE_OVER_WIFI = "update_over_wifi";
    String KEY_LAST_UPDATE_CHECK_DATE = "last_update_check_date";
    String KEY_LAST_IMPORT_URL = "key_last_import_rule";
    String KEY_FILTER_RULE_COUNT = "key_filter_rule_count";
    String KEY_ONBOARDING_SHOWN = "key_onboarding_shown";
    String KEY_USER_RULES_STRING = "key_user_rules_string";
    String KEY_DISABLED_USER_RULES = "key_disabled_user_rules";
    String KEY_WHITELIST_STRING = "key_whitelist_string";
    String KEY_DISABLED_WHITELIST = "key_disabled_whitelist";
    String KEY_LAST_COMMUNICATION_DATE = "key_last_communication_date";
    String KEY_APP_RATED = "key_app_rated";

    /**
     * @return true if filters autoupdate is enabled
     */
    boolean isAutoUpdateFilters();

    /**
     * @param value true if filters autoupdate is enabled
     */
    void setAutoUpdateFilters(boolean value);

    /**
     * @return true if update over wifi only is enabled
     */
    boolean isUpdateOverWifiOnly();

    /**
     * @param value true if update over wifi only is enabled
     */
    void setUpdateOverWifiOnly(boolean value);

    /**
     * @return True if we have shown the onboarding screen
     */
    boolean isOnboardingShown();

    /**
     * Save the flag determining that we have shown onboarding screen
     *
     * @param value True if shown
     */
    void setOnboardingShown(boolean value);

    /**
     * @return Whitelisted domains
     */
    String getWhitelist();

    /**
     * @param whitelist Whitelist contents
     */
    void setWhitelist(String whitelist);

    /**
     * @return Set with disabled whitelist rules
     */
    Set<String> getDisabledWhitelistRules();

    /**
     * @param disabledWhitelistRules Set with disabled whitelist rules
     */
    void setDisabledWhitelistRules(Set<String> disabledWhitelistRules);

    /**
     * @return User filter rules
     */
    String getUserRules();

    /**
     * Set user filter text
     *
     * @param userRules user rules
     */
    void setUserRuleItems(String userRules);

    /**
     * @return Set with disabled user rules
     */
    Set<String> getDisabledUserRules();

    /**
     * @param disabledUserRules Set with disabled user rules
     */
    void setDisabledUserRules(Set<String> disabledUserRules);

    /**
     * @param time Last time updates where checked
     */
    void setLastUpdateCheck(long time);

    /**
     * @return Last time updates where checked
     */
    Date getLastUpdateCheck();

    /**
     * Gets url from which user imported his rules
     *
     * @return last imported url
     */
    String getLastImportUrl();

    /**
     * Saves url from which user imported his rules
     *
     * @param url last url from which user has imported some rules
     */
    void setLastImportUrl(String url);

    /**
     * Set whether we should show useful ads
     */
    void setShowUsefulAds(boolean value);

    /**
     * Whether we should show useful ads
     */
    boolean isShowUsefulAds();

    /**
     * Sets filter rules count
     *
     * @param ruleCount Filter rules count
     */
    void setFilterRuleCount(int ruleCount);

    /**
     * @return Filter rules count
     */
    int getFilterRuleCount();

    /**
     * @return Last time we show `Rate this app` dialog
     */
    Long getLastTimeCommunication();

    /**
     * @param lastTimeCommunication Last time we show `Rate this app` dialog
     */
    void setLastTimeCommunication(Long lastTimeCommunication);

    /**
     * @return {@code True} if user already rate app
     */
    boolean isAppRated();

    /**
     * @param appRated {@code True} if user already rate app
     */
    void setAppRated(boolean appRated);
}
