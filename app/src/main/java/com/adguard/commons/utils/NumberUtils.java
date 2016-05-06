package com.adguard.commons.utils;

import java.math.BigDecimal;

/**
 * Helper class for working with numbers
 */
public class NumberUtils {

    /**
     * Casts string to integer or returns null if value is not integer
     *
     * @param str String to parse
     * @return Integer value or null
     */
    public static Integer toInteger(String str) {

        try {
            return Integer.valueOf(str);
        } catch (Exception ex) {
            return null;
        }
    }

    public static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }
}
