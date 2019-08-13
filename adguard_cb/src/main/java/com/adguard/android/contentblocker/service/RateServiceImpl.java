package com.adguard.android.contentblocker.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.receiver.AlarmReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RateServiceImpl implements RateService {

    private static final Logger LOG = LoggerFactory.getLogger(RateServiceImpl.class);

    // We need only 2 shows
    private static final int MAX_RATE_DIALOG_COUNT = 2;

    // First show is scheduled 24 hours after installation
    private static final long FIRST_FLEX_PERIOD =  24 * 60 * 60 * 1000;

    // Second show is scheduled 7 days after installation
    private static final long SECOND_FLEX_PERIOD = 7 * FIRST_FLEX_PERIOD;

    // Let's check if we should show notification each 6 hours
    private static final long CHECK_PERIOD = 6 * 60 * 60 * 1000;

    private final Context context;

    private PreferencesService preferencesService;

    private Map<Integer, Long> flexPeriods;

    public RateServiceImpl(Context context) {
        this.context = context;
        this.preferencesService = ServiceLocator.getInstance(context).getPreferencesService();
        fillFlexMap();
        checkFirstLaunch();
    }

    @SuppressWarnings("UseSparseArrays")
    private void fillFlexMap() {
        flexPeriods = new HashMap<>();
        flexPeriods.put(0, FIRST_FLEX_PERIOD);
        flexPeriods.put(1, SECOND_FLEX_PERIOD);
    }

    @Override
    public void scheduleRateNotificationShow() {
        if (!shouldShowNotification()) {
            return;
        }

        Intent alarmIntent = new Intent(context, AlarmReceiver.class).setAction(AlarmReceiver.SHOW_RATE_DIALOG_ACTION);
        LOG.info("Starting scheduler of notification show");
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + CHECK_PERIOD, broadcastIntent);
        } else {
            LOG.info("Failed to schedule notification show");
        }
    }

    @Override
    public void showRateNotification() {
//        if (!shouldShowNotification()) {
//            scheduleRateNotificationShow();
//            return;
//        }
//
//        Long flex = flexPeriods.get(preferencesService.getRateAppDialogCount());
//        if (flex == null) {
//            flex = (long) 1000;
//        }
//
//        long installationTime = preferencesService.getInstallationTime();
//        if (System.currentTimeMillis() - installationTime < flex) {
//            return;
//        }

        ServiceLocator.getInstance(context).getNotificationService().showRateAppNotification();
        preferencesService.increaseRateAppDialogCount();
        scheduleRateNotificationShow();
    }

    private void checkFirstLaunch() {
        if (preferencesService.getInstallationTime() == 0L) {
            // It's first launch. We need to set installation time to current
            preferencesService.setInstallationTime(System.currentTimeMillis());
        }
    }

    /**
     * @return {@code True} if app is still not rated and we show notification less than {@link MAX_RATE_DIALOG_COUNT} times
     */
    private boolean shouldShowNotification() {
        if (preferencesService.isAppRated()) {
            return false;
        }

        int showsCount = preferencesService.getRateAppDialogCount();
        return showsCount < MAX_RATE_DIALOG_COUNT;
    }
}
