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