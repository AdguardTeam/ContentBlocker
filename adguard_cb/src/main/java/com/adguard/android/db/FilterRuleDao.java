/**
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
