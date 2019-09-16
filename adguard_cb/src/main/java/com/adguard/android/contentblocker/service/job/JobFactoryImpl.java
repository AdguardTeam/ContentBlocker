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
                    () -> {
                        filterService.tryUpdateFilters();
                        return true;
                    },
                    UPDATE_FILTERS_PERIOD
            );
        }
    }

    class ShowRateNotificationImpl extends AbstractJob {
        private static int MAX_RATE_DIALOG_COUNT = 2;
        private static final long FIRST_FLEX_PERIOD =  TimeUnit.DAYS.toMillis(1L);
        private static final long SECOND_FLEX_PERIOD = TimeUnit.DAYS.toMillis(7L);
        private static final long BACKOFF_PERIOD = TimeUnit.HOURS.toMillis(12L);

        ShowRateNotificationImpl(NotificationService notificationService, PreferencesService preferencesService, JobService jobService) {
            super(
                    Id.RATE_NOTIFICATION,
                    () -> {
                        int count = preferencesService.getRateAppDialogCount();
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
