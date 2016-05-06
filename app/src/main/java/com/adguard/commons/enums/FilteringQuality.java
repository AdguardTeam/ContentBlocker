package com.adguard.commons.enums;

import java.util.HashMap;
import java.util.Map;

public enum FilteringQuality {

    // No cosmetic filtering rules
    SIMPLE(0),

    // No high-quality rules
    SPEEDY(1),

    // Full and high-quality filtering
    FULL(2);

    private static Map<Integer, FilteringQuality> lookup = new HashMap<>();

    static {
        for (FilteringQuality s : values()) {
            lookup.put(s.getCode(), s);
        }
    }

    public static FilteringQuality getByCode(int code) {
        return lookup.get(code);
    }

    private final int code;

    FilteringQuality(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
