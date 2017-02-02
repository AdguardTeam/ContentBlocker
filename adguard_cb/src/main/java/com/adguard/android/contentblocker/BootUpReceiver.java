package com.adguard.android.contentblocker;

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
