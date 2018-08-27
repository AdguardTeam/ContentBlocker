package com.adguard.android.contentblocker.commons;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PackageUtils {
    /**
     * Gets our package version name
     *
     * @param context App context
     * @return Version name string
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            if (packageInfo != null) {
                return packageInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0";
        }

        // Default version name
        return "1.0";
    }
}
