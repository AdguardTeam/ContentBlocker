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
package com.adguard.commons.lang;

import org.apache.commons.lang3.StringUtils;

public class StringHelperUtils {

    public static String lowerCaseAscii(String s) {
        if (s == null) return null;

        int len = s.length();
        char[] buf = new char[len];
        s.getChars(0, len, buf, 0);
        for (int i=0;i<len;i++) {
            if (buf[i] >= 'A' && buf[i] <= 'Z')
                buf[i] += 0x20;
        }

        return new String(buf);
    }

    public static boolean containsIgnoreCaseAscii(String str, String searchStr) {
        return StringUtils.contains(lowerCaseAscii(str), lowerCaseAscii(searchStr));
    }

    public static boolean containsIgnoreCase(String where, String what) {
        final int length = what.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = where.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = where.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (where.regionMatches(true, i, what, 0, length))
                return true;
        }

        return false;
    }

    // Main method: perfroms speed analysis on different contains methods
    // (case ignored)
    /*public static void main(String[] args) throws Exception {
        final String src = "http://google.com/dkfljgfdg/.dfgjdfg/sdfkljsfd.py";
        final String what = "dg/.df";

        long start, end;
        final int N = 10000000;

        start = System.nanoTime();
        for (int i = 0; i < N; i++)
            containsIgnoreCase(src, what);
        end = System.nanoTime();
        System.out.println("Case 1 took " + ((end - start) / 1000000) + "ms");

        start = System.nanoTime();
        for (int i = 0; i < N; i++)
            containsIgnoreCaseAscii(src, what);
        end = System.nanoTime();
        System.out.println("Case 2 took " + ((end - start) / 1000000) + "ms");
    }*/

}
