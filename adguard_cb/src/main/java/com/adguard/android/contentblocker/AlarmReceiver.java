package com.adguard.android.contentblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.adguard.android.ServiceLocator;
import com.adguard.commons.concurrent.DispatcherTask;
import com.adguard.commons.concurrent.DispatcherThreadPool;

/**
 * created by nkartyshov
 * date: 11.10.16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String UPDATE_FILTER_ACTION = "com.adguard.contentblocker.UPDATE_FILTER";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        if (UPDATE_FILTER_ACTION.equals(action)) {
            DispatcherThreadPool.getInstance().submit(new DispatcherTask() {
                @Override
                public void execute() throws Exception {
                    ServiceLocator.getInstance(context).getFilterService().checkFilterUpdates(false);
                }
            });
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            ServiceLocator.getInstance(context).getFilterService().scheduleFiltersUpdate();
        }
    }
}
