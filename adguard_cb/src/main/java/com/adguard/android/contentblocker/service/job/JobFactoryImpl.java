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
package com.adguard.android.contentblocker.service.job;

import com.adguard.android.contentblocker.service.FilterService;
import com.adguard.android.contentblocker.service.NotificationService;
import com.adguard.android.contentblocker.service.PreferencesService;

import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link JobFactory} factory.
 */
interface JobFactoryImpl {
    long UPDATE_FILTERS_PERIOD = TimeUnit.HOURS.toMillis(1L);

    class UpdateFiltersJobImpl extends AbstractJob {
        UpdateFiltersJobImpl(FilterService filterService) {
            super(
                    Id.FILTERS,
                    filterService::tryUpdateFilters,
                    UPDATE_FILTERS_PERIOD
            );
        }
    }

    class ShowRateNotificationImpl extends AbstractJob {
        private static int MAX_RATE_DIALOG_COUNT = 2;
        private static final long FIRST_FLEX_PERIOD =  TimeUnit.DAYS.toMillis(1L);
        private static final long SECOND_FLEX_PERIOD = TimeUnit.DAYS.toMillis(7L);
        private static final long BACKOFF_PERIOD = TimeUnit.HOURS.toMillis(6L);

        ShowRateNotificationImpl(NotificationService notificationService, PreferencesService preferencesService, JobService jobService) {
            super(
                    Id.RATE_NOTIFICATION,
                    () -> {
                        int count = preferencesService.getRateAppDialogCount();
                        if (count >= MAX_RATE_DIALOG_COUNT) {
                            jobService.cancelJobs(Id.RATE_NOTIFICATION);
                            return false;
                        }
                        // First show is scheduled 24 hours after installation
                        // Second show is scheduled 7 days after installation
                        long flexPeriod = count == 0 ? FIRST_FLEX_PERIOD : SECOND_FLEX_PERIOD;

                        long installationTime = preferencesService.getInstallationTime();
                        if (System.currentTimeMillis() - installationTime < flexPeriod) {
                            return false;
                        }

                        notificationService.showRateAppNotification();
                        preferencesService.increaseRateAppDialogCount();
                        if (count == MAX_RATE_DIALOG_COUNT - 1) {
                            jobService.cancelJobs(Id.RATE_NOTIFICATION);
                        }
                        return true;
                    },
                    () -> !preferencesService.isAppRated() && preferencesService.getRateAppDialogCount() < MAX_RATE_DIALOG_COUNT,
                    FIRST_FLEX_PERIOD,
                    BACKOFF_PERIOD
            );
        }
    }
}
