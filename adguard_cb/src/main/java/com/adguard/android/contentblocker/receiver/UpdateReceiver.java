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
package com.adguard.android.contentblocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.service.FilterService;

import org.apache.commons.lang3.StringUtils;

/**
 * This receiver is necessary in case if application was installed but not launched.
 * We need to inform browsers about our filters and enable content blocker
 */
public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!StringUtils.equals(intent.getAction(), "android.intent.action.PACKAGE_REPLACED")) {
            return;
        }
        ServiceLocator.getInstance(context).getFilterService().enableContentBlocker(context);
    }
}
