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

import android.app.Activity;
import android.app.ProgressDialog;

import com.adguard.android.contentblocker.model.FilterList;

import java.util.List;
import java.util.Set;

/**
 * Service that manages ad filters
 */
public interface FilterService {

    /**
     * Try to update the filters.
     * <p>
     * The method is not asynchronous!
     */
    void tryUpdateFilters();

    /**
     * Checks for filter updates and shows waiting dialog on UI
     *
     * @param activity Activity
     */
    void checkFiltersUpdates(Activity activity);

    /**
     * @return List of filters
     */
    List<FilterList> getFilters();

    /**
     * @return Filter list count
     */
    int getFilterListCount();

    /**
     * @return Enabled filter count
     */
    int getEnabledFilterListCount();

    /**
     * @return Filter rules count
     */
    int getFilterRuleCount();

    /**
     * Checks filters updates and returns filters that were updated
     *
     * @return List of filters that were updated
     */
    List<FilterList> checkFilterUpdates(boolean force);

    /**
     * Updates filter status.
     *
     * @param filter  Filter status
     * @param enabled true if filter should be enabled
     */
    void updateFilterEnabled(FilterList filter, boolean enabled);

    /**
     * Downloads and adds batch of user rules from specified url.
     *
     * @param url url
     * @param overwrite True if we should overwrite existing rules
     */
    void importUserRulesFromUrl(Activity activity, String url, boolean overwrite);

    /**
     * @return list of all enabled rules
     */
    List<String> getAllEnabledRules();

    /**
     * Is show useful ads filter enabled
     *
     * @return true if user has decided to show useful ads
     */
    boolean isShowUsefulAds();

    /**
     * Sets show useful ads filter enabled
     *
     * @param value true if user has decided to show useful ads
     */
    void setShowUsefulAds(boolean value);

    /**
     * @return list of enabled filter ids
     */
    List<Integer> getEnabledFilterIds();

    /**
     * Applies new settings and filters
     */
    void applyNewSettings();

    /**
     * @return User filter rules
     */
    String getUserRules();

    /**
     * @return User rules items list
     */
    List<String> getUserRulesItems();

    /**
     * Adds rule to the user filter
     *
     * @param ruleText Item to add
     */
    void addUserRuleItem(String ruleText);

    /**
     * Sets user rules
     *
     * @param userRuleItems User rules
     */
    void setUserRules(String userRuleItems);

    /**
     * Clears user filter
     */
    void clearUserRules();

    /**
     * @return Set with disabled user filter rules
     */
    Set<String> getDisabledUserRules();

    /**
     * Enables specified rule
     *
     * @param ruleText Rule to enable
     */
    void enableUserRule(String ruleText, boolean enabled);

    /**
     * @return Whitelist
     */
    String getWhiteList();

    /**
     * @return Whitelist items
     */
    List<String> getWhiteListItems();

    /**
     * Adds item to whitelist
     *
     * @param item Item to add
     */
    void addWhitelistItem(String item);

    /**
     * @param whitelist Whitelist value to save
     */
    void setWhiteList(String whitelist);

    /**
     * Clears whitelist
     */
    void clearWhiteList();

    /**
     * @return Set with disabled user filter rules
     */
    Set<String> getDisabledWhitelistRules();

    /**
     * Enables specified rule
     *
     * @param ruleText Rule to enable
     * @param enabled  enabled
     */
    void enableWhitelistRule(String ruleText, boolean enabled);

    /**
     * Clears the filters cache and updates filters
     */
    void clearCacheAndUpdateFilters(ProgressDialog progressDialog);
}
