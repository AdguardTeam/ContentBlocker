package com.adguard.android.db;

import com.adguard.android.model.FilterList;

import java.util.List;

/**
 * Data access object for filter lists
 */
public interface FilterListDao {

    /**
     * Inserts new filter
     *
     * @param filterList Filter list to insert
     */
    void insertFilterList(FilterList filterList);

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
