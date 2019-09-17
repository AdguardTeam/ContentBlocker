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
