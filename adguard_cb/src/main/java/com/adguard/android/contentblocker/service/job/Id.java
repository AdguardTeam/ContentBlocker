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
package com.adguard.android.contentblocker.service.job;

import androidx.annotation.NonNull;

/**
 * <pre>
 * IDs for scheduled jobs and updates
 *
 * Id has a tag which should be <b>unique</b>.
 * This tag is used for scheduling and job search.
 */
public enum Id {
    UNKNOWN("Unknown"),

    FILTERS("Filters"),
    RATE_NOTIFICATION("Rate notification")
    ;

    private String tag;

    Id(String tag) {
        this.tag = tag;
    }

    @NonNull
    public static Id valueOfTag(String tag) {
        for (Id id: values()) {
            if (id.tag.equals(tag)) {
                return id;
            }
        }
        return UNKNOWN;
    }

    @NonNull
    public String getTag() {
        return tag;
    }
}