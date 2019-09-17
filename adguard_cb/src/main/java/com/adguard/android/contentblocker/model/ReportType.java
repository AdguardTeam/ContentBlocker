/*
 * This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 * Copyright © 2019 AdGuard Content Blocker. All rights reserved.
 * <p/>
 * AdGuard Content Blocker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * <p/>
 * AdGuard Content Blocker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.contentblocker.model;

import com.adguard.android.contentblocker.R;

public enum ReportType {
    MISSED_AD(R.string.reportTypeMissedAd),
    INCORRECT_BLOCKING(R.string.reportTypeIncorrectBlocking),
    BUG_REPORT(R.string.reportTypeBugReport),
    FEATURE_REQUEST(R.string.reportTypeFeatureRequest),
    CUSTOM(R.string.reportTypeCustom);

    private int stringId;

    ReportType(int stringId) {
        this.stringId = stringId;
    }

    public int getStringId() {
        return stringId;
    }
}
