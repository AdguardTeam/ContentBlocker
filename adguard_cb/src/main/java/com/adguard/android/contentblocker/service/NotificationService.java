package com.adguard.android.contentblocker.service;

/**
 * Application notifications service. Responsible for notifications and toast messages.
 */
public interface NotificationService {
    /**
     * Shows toast with the specified text
     *
     * @param textResId Text resource Id
     */
    void showToast(int textResId);

    /**
     * Shows toast with the specified text
     *
     * @param message  messagen
     */
    void showToast(String message);

    /**
     * Shows toast with the specified text
     *
     * @param message  message
     * @param duration duration
     */
    void showToast(String message, int duration);

    /**
     * Shows a notification asking user to rate this app
     */
    void showRateAppNotification();
}
