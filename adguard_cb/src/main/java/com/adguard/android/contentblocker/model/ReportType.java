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
