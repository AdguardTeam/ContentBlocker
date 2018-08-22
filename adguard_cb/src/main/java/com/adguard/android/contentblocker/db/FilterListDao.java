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
package com.adguard.android.contentblocker.db;


import com.adguard.android.contentblocker.model.FilterList;

import java.util.List;

/**
 * Data access object for filter lists
 */
public interface FilterListDao {

    /**
     * Selects all filter lists
     *
     * @return All filter lists
     */
    List<FilterList> selectFilterLists();

    /**
     * Selects filter list by specified id.
     *
     * @param filterListId Filter list ID
     * @return Filter list
     */
    FilterList selectFilterList(int filterListId);

    /**
     * Gets the size of filters db
     *
     * @return filter count
     */
    int getFilterListCount();

    /**
     * Get count of the the filter rules enabled
     *
     * @return count of the the filter rules enabled
     */
    int getEnabledFilterListCount();

    /**
     * Update selected state of this filter
     * @param filter Filter for updating status
     * @param enabled true for enabled false for disabled state
     */
    void updateFilterEnabled(FilterList filter, boolean enabled);

    /**
     * Update filter id DB
     * @param filter filter with new info
     */
    void updateFilter(FilterList filter);
}
