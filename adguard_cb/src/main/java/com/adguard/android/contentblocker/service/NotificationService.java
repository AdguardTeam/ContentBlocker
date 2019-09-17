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
