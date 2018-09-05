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
package com.adguard.android.contentblocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.service.FilterService;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Executors;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String UPDATE_FILTER_ACTION = "com.adguard.contentblocker.UPDATE_FILTER";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent == null || !StringUtils.equalsIgnoreCase(UPDATE_FILTER_ACTION, intent.getAction())) {
            return;
        }

        final FilterService filterService = ServiceLocator.getInstance(context).getFilterService();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                filterService.tryUpdateFilters();
            }
        });
    }
}
