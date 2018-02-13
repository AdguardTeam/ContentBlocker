/*
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
package com.adguard.android.contentblocker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.adguard.android.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Boot up receiver class.
 *  Used to receive a boot completed call back and start filters update scheduler
 */
public class BootUpReceiver extends BroadcastReceiver {

    private final static Logger LOG = LoggerFactory.getLogger(BootUpReceiver.class);
    private final static String ACTION_QUICK_BOOT = "android.intent.action.QUICKBOOT_POWERON";
    private final static String ACTION_QUICK_BOOT_HTC = "com.htc.intent.action.QUICKBOOT_POWERON";

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.info("Receiver got an action {}", intent.getAction());

        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) &&
                !ACTION_QUICK_BOOT.equals(intent.getAction()) &&
                !ACTION_QUICK_BOOT_HTC.equals(intent.getAction())) {
            return;
        }

        ServiceLocator.getInstance(context).getFilterService().scheduleFiltersUpdate();
    }
}
