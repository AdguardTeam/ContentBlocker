/*
 * This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 * Copyright © 2018 AdGuard Content Blocker. All rights reserved.
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
package com.adguard.android.contentblocker.ui.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.service.NotificationService;
import com.adguard.android.contentblocker.ui.MainActivity;
import com.adguard.android.contentblocker.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Activity navigation helper
 */
public class NavigationHelper {

    private static final Logger LOG = LoggerFactory.getLogger(NavigationHelper.class);

    /**
     * Redirects to activityClass activity.
     *
     * @param from          Activity from
     * @param activityClass Activity class
     */
    public static void redirectToActivity(Activity from, Class activityClass) {
        Intent intent = new Intent(from.getApplicationContext(), activityClass);
        from.startActivity(intent);
    }

    /**
     * Opens web browser at specified url
     *
     * @param from Context
     * @param url  Url to open
     */
    public static void redirectToWebSite(Context from, String url) {
        redirectUsingCustomTab(from, url);
    }

    private static void redirectUsingCustomTab(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

            // set desired toolbar colors
            intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            // Doesn't work anyway
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.white));

            CustomTabsIntent customTabsIntent = intentBuilder.build();
            customTabsIntent.launchUrl(context, uri);
        } catch (ActivityNotFoundException e) {
            LOG.error("Error while launching an URL: {}", url, e);
            NotificationService notificationService = ServiceLocator.getInstance(context).getNotificationService();
            notificationService.showToast(R.string.progressGenericErrorText);
        }
    }

    /**
     * Redirects to quit application activity thorough MainActivity.
     * Look MainActivity.onNewIntent() for details.
     *
     * @param from Activity
     */
    public static void quit(Activity from) {
        Intent intent = new Intent(from, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        from.startActivity(intent);
        from.finish();
    }
}
