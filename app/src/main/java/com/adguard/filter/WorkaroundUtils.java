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
