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
