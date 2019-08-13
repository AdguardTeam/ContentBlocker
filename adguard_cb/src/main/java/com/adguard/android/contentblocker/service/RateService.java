package com.adguard.android.contentblocker.service;

/**
 * Interface to manage rate notifications and check if user rates app
 */
public interface RateService {

    /**
     * Schedules rate notification show if necessary
     */
    void scheduleRateNotificationShow();

    /**
     * Shows rate notification
     */
    void showRateNotification();
}
