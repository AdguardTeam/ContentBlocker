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
