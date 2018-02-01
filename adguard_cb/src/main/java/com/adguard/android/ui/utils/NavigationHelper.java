package com.adguard.android.ui.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;

import com.adguard.android.contentblocker.MainActivity;
import com.adguard.android.contentblocker.R;

/**
 * Activity navigation helper
 */
public class NavigationHelper {

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
    public static void redirectToWebSite(Activity from, String url) {
        redirectUsingCustomTab(from, url);
    }

    private static void redirectUsingCustomTab(Activity context, String url) {
        Uri uri = Uri.parse(url);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        // set desired toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        // Doesn't work anyway
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.white));

        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(context, uri);
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
