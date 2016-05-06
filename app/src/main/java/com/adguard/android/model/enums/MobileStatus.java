package com.adguard.android.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration with possible mobile statuses
 */
public enum MobileStatus {

    /**
     * App is in FREE mode (no premium features)
     */
    FREE("\"FREE\""),

    /**
     * App is in PREMIUM mode (premium feature enabled)
     */
    PREMIUM("\"PREMIUM\""),

    /**
     * PREMIUM subscription has been expired.
     * App should work in FREE mode.
     */
    EXPIRED("\"EXPIRED\""),

    /**
     * There is an error with the license status.
     * License key could have been expired,
     * max computers count could have been exceed,
     * or it could be blocked.
     * <p/>
     * App should work in FREE mode in this case.
     */
    ERROR("\"ERROR\""),

	/**
	 * License trial status.
	 * App should work in FREE mode.
	 */
	TRIAL("\"TRIAL\"")
	;

    MobileStatus(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }

    private static Map<String, MobileStatus> lookup = new HashMap<>();

    static {
        for (MobileStatus s : values()) {
            lookup.put(s.getCode(), s);
        }
    }

    public static MobileStatus getByCode(String code) {
        return lookup.get(code);
    }
}
