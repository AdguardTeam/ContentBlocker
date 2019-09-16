package com.adguard.android.contentblocker.service.job;

import android.content.Context;

import com.adguard.android.contentblocker.ServiceLocator;

/**
 * Factory for the creation of tasks.
 */
class JobFactory {

    /**
     * Gets instance of job for running.
     *
     * @param context context
     * @param id id of job
     * @return instance of {@link Job} or {@code null} if factory cannot create a job with same id
     */
    static Job getJob(Context context, Id id) {
        return getJob(ServiceLocator.getInstance(context), id);
    }

    /**
     * Same as {@link #getJob(Context, Id)} but with {@link ServiceLocator} parameter instead {@link Context}.
     *
     * @param serviceLocator instance of {@link ServiceLocator}
     * @param id id of job
     * @return instance of {@link Job} or {@code null} if factory cannot create a job with same id
     */
    static Job getJob(ServiceLocator serviceLocator, Id id) {
        switch (id) {
            case FILTERS:
                return new JobFactoryImpl.UpdateFiltersJobImpl(serviceLocator.getFilterService());
            case RATE_NOTIFICATION:
                return new JobFactoryImpl.ShowRateNotificationImpl(serviceLocator.getNotificationService(),
                        serviceLocator.getPreferencesService(), serviceLocator.getJobService());
            case UNKNOWN:
            default:
                return null;
        }
    }
}
