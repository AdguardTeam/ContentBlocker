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
import com.adguard.android.model.FilterList;

import java.util.List;
import java.util.Set;

/**
 * Service that manages ad filters
 */
public interface FilterService {

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
     * Schedules filters updates periodic job.
     */
    void scheduleFiltersUpdate();

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
	 */
    void importUserRulesFromUrl(Activity activity, String url);

    /**
     * @return list of all enabled rules
     */
    List<String> getAllEnabledRules(boolean useCosmetics);

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
}
