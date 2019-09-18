/*
 This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2018 AdGuard Content Blocker. All rights reserved.

 AdGuard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 AdGuard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.contentblocker.ui.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.Surface;

import java.util.Date;

/**
 * Activity utils methods.
 */
public class ActivityUtils {

    /**
     * Formats a date into a datetime string.
     *
     * @param dateTime dateTime
     * @return the formatted time string
     */
    public static String formatDateTime(Context context, Date dateTime) {
        return formatDate(context, dateTime) + " " + formatTime(context, dateTime);
    }

    public static void startMarket(Context context, String packageName, String referrer) {
        String referrerParam = referrer != null ? "&referrer=" + referrer : "";
        try {
            Uri uri = Uri.parse("market://details?id=" + packageName + referrerParam);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName + referrerParam));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * Formats date string
     *
     * @param date Date
     * @return Formatted date
     */
    static String formatDate(Context context, Date date) {
        return DateUtils.formatDateTime(context, date.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }

    /**
     * Formats date to time string
     *
     * @param time Time
     * @return Formatted time
     */
    static String formatTime(Context context, Date time) {
        return DateUtils.formatDateTime(context, time.getTime(), DateUtils.FORMAT_SHOW_TIME);
    }

    /**
     * Locks specified activity's orientation until it is unlocked or recreated.
     *
     * @param activity activity
     */
    static void lockOrientation(Activity activity) {
        Configuration config = activity.getResources().getConfiguration();
        final int deviceOrientation = config.orientation;
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int orientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
        if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_180)
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        } else if (deviceOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_270)
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }

        activity.setRequestedOrientation(orientation);
    }

    /**
     * Unlocks specified activity's orientation.
     *
     * @param activity activity
     */
    static void unlockOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
