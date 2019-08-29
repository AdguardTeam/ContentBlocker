/**
 * This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 * Copyright © 2018 AdGuard Content Blocker. All rights reserved.
 * <p>
 * AdGuard Content Blocker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * <p>
 * AdGuard Content Blocker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.contentblocker.commons;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.appcompat.app.AlertDialog;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.service.NotificationService;
import com.adguard.android.contentblocker.ui.utils.ActivityUtils;
import com.adguard.android.contentblocker.ui.utils.NavigationHelper;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Some functions for working with browsers
 */
public class BrowserUtils {

    public static final String YANDEX = "yandex";
    public static final String SAMSUNG = "samsung";

    public static final String YANDEX_BROWSER_PACKAGE = "com.yandex.browser";
    public static final String SAMSUNG_BROWSER_PACKAGE = "com.sec.android.app.sbrowser";
    private static final String SAMSUNG_CONTENT_BLOCKER_ACTION = "com.samsung.android.sbrowser.contentBlocker.ACTION_SETTING";
    private static final String YANDEX_CONTENT_BLOCKER_ACTION = "com.yandex.browser.contentBlocker.ACTION_SETTING";

    private static final String SAMSUNG_PACKAGE_PREFIX = "com.sec.";

    private static final String REFERRER = "adguard1";

    private static final List<String> yandexBrowserPackageList = new ArrayList<>();
    private static final List<String> samsungBrowserPackageList = new ArrayList<>();
    public static final String TAG = "BrowserUtils";

    static {
        yandexBrowserPackageList.add("com.yandex.browser");
        yandexBrowserPackageList.add("com.yandex.browser.beta");
        yandexBrowserPackageList.add("com.yandex.browser.alpha");

        samsungBrowserPackageList.add("com.sec.android.app.sbrowser");
        samsungBrowserPackageList.add("com.sec.android.app.sbrowser.beta");
    }

    public static Set<String> getBrowsersAvailableByIntent(Context context) {
        Set<String> result = new HashSet<>();
        Intent intent = new Intent();
        intent.setAction(SAMSUNG_CONTENT_BLOCKER_ACTION);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list) {
                if (info.activityInfo.packageName.contains(YANDEX)) {
                    result.add(YANDEX);
                } else if (info.activityInfo.packageName.contains(SAMSUNG_PACKAGE_PREFIX) || info.activityInfo.packageName.contains(SAMSUNG)) {
                    result.add(SAMSUNG);
                }
            }
        }

        return result;
    }

    public static Set<String> getBrowsersAvailableByPackage(Context context) {
        Set<String> result = new HashSet<>();
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            if (packageInfo.packageName.startsWith(YANDEX_BROWSER_PACKAGE)) {
                result.add(YANDEX);
            } else if (packageInfo.packageName.startsWith(SAMSUNG_BROWSER_PACKAGE)) {
                result.add(SAMSUNG);
            }
        }

        return result;
    }

    public static Set<String> getKnownBrowsers() {
        Set<String> result = new HashSet<>();
        result.addAll(yandexBrowserPackageList);
        result.addAll(samsungBrowserPackageList);
        return result;
    }

    public static void openSamsungBlockingOptions(Context context) {
        Intent intent = new Intent();
        intent.setAction(SAMSUNG_CONTENT_BLOCKER_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list) {
                if (info.activityInfo.packageName.contains(SAMSUNG_PACKAGE_PREFIX) || info.activityInfo.packageName.contains(SAMSUNG)) {
                    intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                    startActivity(context, intent);
                    return;
                }
            }
        }
    }

    public static boolean isSamsungBrowserAvailable(Context context) {
        Intent intent = new Intent();
        intent.setAction(SAMSUNG_CONTENT_BLOCKER_ACTION);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list) {
                if (info.activityInfo.packageName.contains(SAMSUNG_PACKAGE_PREFIX) || info.activityInfo.packageName.contains(SAMSUNG)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void openYandexBlockingOptions(Context context) {
        Intent intent = new Intent();
        intent.setAction(YANDEX_CONTENT_BLOCKER_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            startActivity(context, intent);
            return;
        }

        // For samsung-type action in Yandex browser
        intent.setAction(SAMSUNG_CONTENT_BLOCKER_ACTION);
        list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {

            ComponentName componentName = getYandexBrowser(context, SAMSUNG_CONTENT_BLOCKER_ACTION);
            if (componentName != null) {
                intent.setClassName(componentName.getPackageName(), componentName.getClassName());
            }

            startActivity(context, intent);
        }
    }

    public static boolean isYandexBrowserAvailable(Context context) {
        Intent intent = new Intent();
        intent.setAction(YANDEX_CONTENT_BLOCKER_ACTION);
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list) {
                if (info.activityInfo.packageName.contains(BrowserUtils.YANDEX)) {
                    return true;
                }
            }
        }

        // For samsung-type action in Yandex browser
        intent.setAction(SAMSUNG_CONTENT_BLOCKER_ACTION);
        list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list) {
                if (info.activityInfo.packageName.contains(BrowserUtils.YANDEX)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressLint("InflateParams")
    public static void showBrowserInstallDialog(final Context context) {
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.select_browser_dialog, null);

        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.AlertDialog)
            .setNegativeButton(android.R.string.cancel, null)
            .setView(dialogLayout).create();

        View browserItem = dialogLayout.findViewById(R.id.browser_yandex);
        browserItem.setOnClickListener(v -> {
            ActivityUtils.startMarket(context, YANDEX_BROWSER_PACKAGE, REFERRER);
            dialog.dismiss();
        });

        browserItem = dialogLayout.findViewById(R.id.browser_samsung);
        browserItem.setOnClickListener(v -> {
            ActivityUtils.startMarket(context, SAMSUNG_BROWSER_PACKAGE, null);
            dialog.dismiss();
        });

        dialogLayout.findViewById(R.id.others_product_card).setOnClickListener(v -> {
            showProductsDialog(context);
        });

        dialog.show();

        // Center the button
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1.0f;
        layoutParams.gravity = Gravity.CENTER; //this is layout_gravity
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
    }

    @SuppressLint("InflateParams")
    public static void showProductsDialog(final Context context) {
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.products_dialog, null);

        final AlertDialog dialog = new AlertDialog.Builder(context, R.style.AlertDialog)
                .setNegativeButton(R.string.back, null)
                .setView(dialogLayout).create();

        TextView textView = dialogLayout.findViewById(R.id.dialog_text);
        textView.setText(Html.fromHtml(context.getString(R.string.chrome_dialog_text)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        dialogLayout.findViewById(R.id.go_to_products).setOnClickListener(v -> {
            String url = AppLink.Website.getOtherProductUrl(context, "chrome_dialog");
            NavigationHelper.openWebSite(context, url);
        });

        dialog.show();

        // Center the button
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1.0f;
        layoutParams.gravity = Gravity.CENTER; //this is layout_gravity
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
    }

    public static void startYandexBrowser(Context context) {
        ComponentName componentName = getYandexBrowser(context, Intent.ACTION_MAIN);

        if (componentName != null) {
            startBrowser(context, componentName);
        }
    }

    public static void startSamsungBrowser(Context context) {
        ComponentName componentName = getSamsungBrowser(context);

        if (componentName != null) {
            startBrowser(context, componentName);
        }
    }

    private static void startBrowser(Context context, ComponentName component) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(component);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    // https://github.com/AdguardTeam/ContentBlocker/issues/56
    private static ComponentName getSamsungBrowser(Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> installedPackages = context.getPackageManager().queryIntentActivities(mainIntent, 0);

        ArrayList<ActivityInfo> samsungActivities = new ArrayList<>();
        for (ResolveInfo installedPackage : installedPackages) {
            if (installedPackage.activityInfo.packageName.startsWith(SAMSUNG_BROWSER_PACKAGE)) {
                samsungActivities.add(installedPackage.activityInfo);
            }
        }

        if (CollectionUtils.isNotEmpty(samsungActivities)) {
            Collections.sort(samsungActivities, new Comparator<ActivityInfo>() {
                @Override
                public int compare(ActivityInfo lhs, ActivityInfo rhs) {
                    return lhs.packageName.compareTo(rhs.packageName);
                }
            });

            ActivityInfo activityInfo = samsungActivities.get(0);
            return new ComponentName(activityInfo.packageName, activityInfo.name);

        }

        return null;
    }

    // https://github.com/AdguardTeam/ContentBlocker/issues/53
    private static ComponentName getYandexBrowser(Context context, String action) {
        Intent mainIntent = new Intent();
        mainIntent.setAction(action);

        for (String packageName : yandexBrowserPackageList) {
            mainIntent.setPackage(packageName);

            List<ResolveInfo> installedPackages = context.getPackageManager().queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);

            if (!installedPackages.isEmpty()) {
                ResolveInfo resolveInfo = installedPackages.get(0);
                return new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
            }
        }

        return null;
    }

    /**
     * Starts activity and shows notification if activity not found
     *
     * @param context context
     * @param intent intent
     */
    private static void startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            NotificationService notificationService = ServiceLocator.getInstance(context).getNotificationService();
            notificationService.showToast(R.string.progressGenericErrorText);
        }
    }
}
