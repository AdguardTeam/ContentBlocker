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
