package com.adguard.android.db;

import java.util.List;

/**
 * Data access object for filter rules
 */
public interface FilterRuleDao {

    /**
     * Selects rules by filter ids list
     *
     * @param filterIds Filter ID list
     * @return List of rules
     */
    List<String> selectRuleTexts(List<Integer> filterIds, boolean useCosmetics);

    /**
     * Inserts batch of rules to the specified filter.
     *
     * @param filterId Filter to insert rules to
     * @param rules    Rules to be inserted
     */
    void setFilterRules(int filterId, List<String> rules);
}
