package com.adguard.android.contentblocker.ui.utils;

import android.content.Context;

import com.adguard.android.contentblocker.BuildConfig;
import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.commons.PackageUtils;
import com.adguard.android.contentblocker.service.FilterService;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class ReportToolUtils {
    public static String getUrl(Context context) {
        String versionName = PackageUtils.getVersionName(context);
        FilterService filterService = ServiceLocator.getInstance(context).getFilterService();

        List<Integer> enabledFilterIds = filterService.getEnabledFilterIds();
        String filters = StringUtils.join(enabledFilterIds, ".");

        return String.format("%s?product_type=Con&product_version=%s&browser=Other&filters=%s",
                BuildConfig.reportToolUrl, versionName, filters);
    }
}
