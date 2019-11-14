package com.adguard.android.contentblocker.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.service.NotificationServiceImpl;
import com.adguard.android.contentblocker.ui.MainActivity;
import com.adguard.android.contentblocker.ui.utils.NavigationHelper;

import org.apache.commons.lang3.StringUtils;

import static android.content.Context.NOTIFICATION_SERVICE;

public class StarsCountReceiver extends BroadcastReceiver {

    private static final long REDIRECTION_DELAY = 300;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (StringUtils.isEmpty(action)) {
            return;
        }

        int count = ServiceLocator.getInstance(context).getNotificationService().showRateAppNotification(action);
        // If something wrong with action, the user can rate us anyway.
        if (count == 0) {
            NavigationHelper.redirectToPlayMarket(context);
        } else {
            redirectWithDelay(context, count);
        }
    }

    /**
     * Redirects to Google Play market or to feedback dialog.
     * {@link Handler#postDelayed} used to fill selected stars count explicit before redirection
     *
     * @param count Selected stars count
     */
    private void redirectWithDelay(Context context, final int count) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                // Cancel current 'rate app' notification and close notification bar before redirection
                notificationManager.cancel(NotificationServiceImpl.RATE_NOTIFICATION_ID);
                Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                context.sendBroadcast(closeIntent);
            }

            if (count > 3) {
                NavigationHelper.redirectToPlayMarket(context);
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt(MainActivity.STARS_COUNT, count);
                NavigationHelper.redirectToActivity(context, MainActivity.class, bundle);
            }
        }, REDIRECTION_DELAY);
    }
}
