package com.adguard.android.contentblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.adguard.android.ServiceLocator;
import com.adguard.android.model.FilterList;
import com.adguard.android.service.FilterService;
import com.adguard.android.service.PreferencesService;
import com.adguard.commons.concurrent.DispatcherTask;
import com.adguard.commons.concurrent.DispatcherThreadPool;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

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
                    FilterService filterService = ServiceLocator.getInstance(context).getFilterService();
                    PreferencesService preferencesService = ServiceLocator.getInstance(context).getPreferencesService();

                    filterService.checkFilterUpdates(false);
                    filterService.applyNewSettings();
                    preferencesService.setLastUpdateCheck(System.currentTimeMillis());
                }
            });
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            ServiceLocator.getInstance(context).getFilterService().scheduleFiltersUpdate();
        }
    }
}
