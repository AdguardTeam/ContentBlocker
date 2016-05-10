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
package com.adguard.filter;

import android.content.Context;
import com.adguard.commons.enums.FilteringQuality;

/**
 * Different workarounds
 */
public class WorkaroundUtils {

    /**
     * We will use ContentEditingHtmlParser for all devices
     * whose speed rank is lower than this limit.
     * <p/>
     * This value is experimental.
     * For Samsung Galaxy Tab 3 7.0 speed rank is 500-600 and
     * it is too slow.
     */
    private final static int SPEED_RANK_LIMIT = 150;

    /**
     * We use speed rank to determine if we need to disable
     * cosmetic rules for low-end devices
     */
    private final static int COSMETIC_SPEED_RANK_LIMIT = 2000;

    /**
     * @param deviceSpeedRank Device speed rank
     * @return true if we should use contentblocker parser by default
     */
    public static FilteringQuality getDefaultFilteringQuality(int deviceSpeedRank) {
        if (deviceSpeedRank > COSMETIC_SPEED_RANK_LIMIT) {
            return FilteringQuality.SIMPLE;
        } else if (deviceSpeedRank > SPEED_RANK_LIMIT) {
            return FilteringQuality.SPEEDY;
        } else {
            return FilteringQuality.FULL;
        }
    }

    /**
     * This method is used to calculate device speed rank.
     * Depending on this rank we use either ContentEditingHtmlParser (which is slow but has good quality)
     * or FastHtmlParser (which is contentblocker but the only thing it does is injecting CSS/JS).
     * <p/>
     * The lower speed rank is better.
     *
     * @return Speed rank.
     */
    public static int calculateDeviceSpeedRank(Context context) {
        // TODO make it true
        return 150;
    }
}
