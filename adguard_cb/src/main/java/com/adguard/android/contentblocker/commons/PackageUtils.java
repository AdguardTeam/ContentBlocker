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
package com.adguard.android.contentblocker.commons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;

public class PackageUtils {
    /**
     * Gets our package version name
     *
     * @param context App context
     * @return Version name string
     */
    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }

        // Default version name
        return "1.0";
    }

    /**
     * Checks Google Play Store is installed or not
     *
     * @param context App context
     * @return Google Play Store is installed or not
     */
    public static boolean isPlayStoreInstalled(Context context){
        try {
            context.getPackageManager().getPackageInfo("com.android.vending", 0);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @SuppressLint("HardwareIds")
    static String getApplicationId(Context context) {
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId != null ? androidId : "Android";
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();

        try {
            return packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
