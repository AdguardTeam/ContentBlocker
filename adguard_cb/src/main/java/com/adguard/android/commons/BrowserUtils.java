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
package com.adguard.android.commons;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.adguard.android.contentblocker.MainActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Some functions for working with browsers
 */
public class BrowserUtils {

    public static final String YANDEX = "yandex";
    public static final String SAMSUNG = "samsung";

    public static Set<String> getBrowsersAvailable(Context context) {
        Set<String> result = new HashSet<>();
        Intent intent = new Intent();
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_SETTING");
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list)
            {
                if (info.activityInfo.packageName.contains(YANDEX)) {
                    result.add(YANDEX);
                } else if (info.activityInfo.packageName.contains("com.sec.") || info.activityInfo.packageName.contains(SAMSUNG)) {
                    result.add(SAMSUNG);
                }
            }
        }

        return result;
    }

    public static void openSamsungBlockingOptions(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_SETTING");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            boolean found = false;
            for (ResolveInfo info : list)
            {
                if (info.activityInfo.packageName.contains("com.sec.") || info.activityInfo.packageName.contains("samsung")) {
                    found = true;
                    intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                }
            }
            if (found) {
                context.startActivity(intent);
            }
        }
    }

    public static boolean isSamsungBrowserAvailable(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_SETTING");
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list)
            {
                if (info.activityInfo.packageName.contains("com.sec.") || info.activityInfo.packageName.contains("samsung")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void openYandexBlockingOptions(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.yandex.browser.contentBlocker.ACTION_SETTING");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            context.startActivity(intent);
            return;
        }

        // For samsung-type action in Yandex browser
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_SETTING");
        list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            boolean found = false;
            for (ResolveInfo info : list)
            {
                if (info.activityInfo.packageName.contains(MainActivity.YANDEX)) {
                    found = true;
                    intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                }
            }
            if (found) {
                context.startActivity(intent);
            }
        }
    }

    public static boolean isYandexBrowserAvailable(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.yandex.browser.contentBlocker.ACTION_SETTING");
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list)
            {
                if (info.activityInfo.packageName.contains(MainActivity.YANDEX)) {
                    return true;
                }
            }
        }

        // For samsung-type action in Yandex browser
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_SETTING");
        list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list)
            {
                if (info.activityInfo.packageName.contains(MainActivity.YANDEX)) {
                    return true;
                }
            }
        }
        return false;
    }
}
