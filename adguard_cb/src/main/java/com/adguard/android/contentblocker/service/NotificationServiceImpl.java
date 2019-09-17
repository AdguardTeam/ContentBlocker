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
package com.adguard.android.contentblocker.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ui.MainActivity;
import com.adguard.android.contentblocker.ui.utils.NavigationHelper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationServiceImpl implements NotificationService {

    private static Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private static final String STARS_COUNT_ACTION_1 = "count_action_1";
    private static final String STARS_COUNT_ACTION_2 = "count_action_2";
    private static final String STARS_COUNT_ACTION_3 = "count_action_3";
    private static final String STARS_COUNT_ACTION_4 = "count_action_4";
    private static final String STARS_COUNT_ACTION_5 = "count_action_5";

    private static final long REDIRECTION_DELAY = 300;
    private static final int RATE_NOTIFICATION_ID = 128;

    private final Context context;
    private final Handler handler;

    private Map<String, CountId> countActions;

    public NotificationServiceImpl(Context context) {
        prepareLooper();
        this.context = context;
        this.handler = new Handler();

        fillActionsMap();
        createNotificationChannel();
        registerStarsCountReceiver();
    }

    @Override
    public void showToast(int textResId) {
        showToast(context.getString(textResId), Toast.LENGTH_LONG);
    }

    @Override
    public void showToast(final String message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    @Override
    public void showToast(final String message, final int duration) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast toast = getToast(message, duration);
            toast.show();
        } else {
            handler.post(() -> {
                Toast toast = getToast(message, duration);
                toast.show();
            });
        }
    }

    @Override
    public void showRateAppNotification() {
        showRateAppNotification(0);
    }

    /**
     * Initializes the current thread as a looper in case if application was started implicitly in non-UI thread
     * This case may occurs after installation via browser menu without app launching.
     * There are no opportunities to create {@link Handler} without this call ¯\_(ツ)_/¯
     */
    private void prepareLooper() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
    }

    /**
     * Fills map with actions, ids and stars count
     */
    private void fillActionsMap() {
        countActions = new HashMap<>();
        countActions.put(STARS_COUNT_ACTION_1, new CountId(R.id.star1, 1));
        countActions.put(STARS_COUNT_ACTION_2, new CountId(R.id.star2, 2));
        countActions.put(STARS_COUNT_ACTION_3, new CountId(R.id.star3, 3));
        countActions.put(STARS_COUNT_ACTION_4, new CountId(R.id.star4, 4));
        countActions.put(STARS_COUNT_ACTION_5, new CountId(R.id.star5, 5));
    }

    /**
     * Registers {@link StarsCountReceiver} with configured {@link IntentFilter}
     */
    private void registerStarsCountReceiver() {
        IntentFilter filter = new IntentFilter();
        for (String action : countActions.keySet()) {
            filter.addAction(action);
        }
        context.registerReceiver(new StarsCountReceiver(), filter);
    }

    /**
     * Creates {@link NotificationChannel}s for API >= 26
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                List<NotificationChannel> notificationChannels = new ArrayList<>();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

                for (NotificationChannelMeta channelMeta : NotificationChannelMeta.values()) {
                    NotificationChannel channel = createNotificationChannel(channelMeta);
                    notificationChannels.add(channel);
                }

                if (notificationManager != null) {
                    notificationManager.createNotificationChannels(notificationChannels);
                } else {
                    LOG.debug("Can't get NotificationManager!");
                }
            } catch (NullPointerException ex) {
                LOG.debug("Exception while creating notification channels \n", ex);
            }
        }
    }

    /**
     * Creates notification channel with default importance and adds it into notificationChannels list
     *
     * @param channelMeta notification channel meta data
     * @return NotificationChannel instance
     */
    @TargetApi(Build.VERSION_CODES.O)
    private NotificationChannel createNotificationChannel(NotificationChannelMeta channelMeta) {

        int notificationChannelImportance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(channelMeta.getChannelId(),
                context.getString(channelMeta.getNameId()), notificationChannelImportance);

        channel.setDescription(context.getString(channelMeta.getDescriptionId()));

        // Badges are annoying
        channel.setShowBadge(false);

        // Disable sound explicitly
        channel.setSound(null, null);
        return channel;
    }

    /**
     * Shows a notification asking user to rate this app
     *
     * @param count count of filled stars
     */
    private void showRateAppNotification(int count) {
        Bundle countBundle = new Bundle();
        countBundle.putInt(MainActivity.STARS_COUNT, count);
        Intent intent = new Intent(context, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtras(countBundle);

        Notification notification = createDefaultNotificationBuilder(NotificationChannelMeta.RATE_APP_CHANNEL, context.getString(R.string.rate_app_dialog_title), context.getString(R.string.rate_app_summary))
            .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT))
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_content_blocker)
            .setDefaults(Notification.DEFAULT_LIGHTS)
            .setPriority( NotificationCompat.PRIORITY_HIGH)
            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
            .setCustomBigContentView(createStarsRemoteViews(count))
            .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(RATE_NOTIFICATION_ID, notification);
        }
    }

    /**
     * Updates stars state (empty/filled)
     *
     * @param filledCount Count of filled stars
     * @param remoteViews {@link RemoteViews} with stars images inside
     */
    private void updateStarsState(int filledCount, RemoteViews remoteViews) {
        int emptyId = R.drawable.ic_star_empty;
        int filledId = R.drawable.ic_star_filled;
        for (CountId countId : countActions.values()) {
            remoteViews.setImageViewResource(countId.getViewId(), filledCount >= countId.getCount() ? filledId : emptyId);
        }
    }

    /**
     * Creates {@link RemoteViews} with empty and filled stars and {@link PendingIntent}s with actions for each view
     *
     * @param filledCount Count of filled stars
     * @return {@link RemoteViews} with stars images inside
     */
    private RemoteViews createStarsRemoteViews(int filledCount) {
        RemoteViews remote = new RemoteViews(context.getPackageName(), R.layout.rate_notification);
        for (Map.Entry<String, CountId> entry : countActions.entrySet()) {
            Intent countIntent = new Intent(entry.getKey());
            remote.setOnClickPendingIntent(entry.getValue().getViewId(), PendingIntent.getBroadcast(context, 0, countIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        updateStarsState(filledCount, remote);
        return remote;
    }

    /**
     * Creates notification builder with default parameters
     *
     * @param meta      {@link NotificationChannelMeta}
     * @param title     Title
     * @param message   Message
     * @return default {@link NotificationCompat.Builder} with given title and message
     */
    private NotificationCompat.Builder createDefaultNotificationBuilder(NotificationChannelMeta meta, CharSequence title, CharSequence message) {
        return new NotificationCompat.Builder(context, meta.getChannelId())
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setColor(context.getResources().getColor(R.color.colorPrimary));
    }

    @SuppressWarnings("InflateParams")
    private Toast getToast(String message, int duration) {
        View v = LayoutInflater.from(context).inflate(R.layout.transient_notification, null);
        TextView tv = v.findViewById(android.R.id.message);
        tv.setTextSize(16);
        tv.setText(message);

        final Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(v);

        return toast;
    }

    /**
     * Redirects to Google Play market or to feedback dialog.
     * {@link Handler#postDelayed} used to fill selected stars count explicit before redirection
     *
     * @param count Selected stars count
     */
    private void redirectWithDelay(final int count) {
        handler.postDelayed(() -> {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                // Cancel current 'rate app' notification and close notification bar before redirection
                notificationManager.cancel(RATE_NOTIFICATION_ID);
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

    /**
     * Receiver to handle broadcast with 'fill stars` actions
     */
    private class StarsCountReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (StringUtils.isEmpty(action)) {
                return;
            }
            CountId countId = countActions.get(action);
            if (countId == null) {
                return;
            }

            showRateAppNotification(countId.getCount());
            int count = countId.getCount();
            redirectWithDelay(count);

        }
    }

    /**
     * Wrapper class for view identifier and corresponding filled stars count
     */
    private class CountId {

        private int viewId;
        private int count;

        /**
         * @param viewId View ID
         * @param count  Count of stars to be filled on view click
         */
        CountId(@IdRes int viewId, int count) {
            this.viewId = viewId;
            this.count = count;
        }

        /**
         * @return ID of View
         */
        int getViewId() {
            return viewId;
        }

        /**
         * @return Count of stars to be filled on view click
         */
        int getCount() {
            return count;
        }
    }

    /**
     * Enum of notification channels
     *
     * Extend it if you need more channels.
     * <b>NOTE</b> that channel ID used to sort channels on the UI
     */
    private enum NotificationChannelMeta {
        RATE_APP_CHANNEL("1", R.string.notification_channel_rate_name, R.string.notification_channel_rate_description);

        private final String channelId;
        private final int nameId;
        private final int descriptionId;

        /**
         * @param channelId     Channel ID
         * @param nameId        Channel name string ID to be show on the UI
         * @param descriptionId Channel description string ID to be show on the UI
         */
        NotificationChannelMeta(String channelId, @StringRes int nameId, @StringRes int descriptionId) {
            this.channelId = channelId;
            this.nameId = nameId;
            this.descriptionId = descriptionId;
        }

        /**
         * @return Channel description string ID to be show on the UI
         */
        @StringRes
        public int getDescriptionId() {
            return descriptionId;
        }

        /**
         * @return Channel name string ID to be show on the UI
         */
        @StringRes
        public int getNameId() {
            return nameId;
        }

        /**
         * @return Channel ID
         */
        public String getChannelId() {
            return channelId;
        }}
}
