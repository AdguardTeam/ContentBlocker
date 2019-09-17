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

import java.util.UUID;

/**
 * <pre>
 * Service for scheduling of application's jobs.
 *
 * If you want to create job you should implement interface {@link Job}.</pre>
 */
public interface JobService {

    /**
     * Schedules jobs using {@link Job} instances.
     * The job will not be scheduled if it's already scheduled.
     *
     * @param ids ids of Jobs
     */
    void scheduleJobs(Id... ids);

    /**
     * Cancels job with same id.
     *
     * @param uuid id of job
     */
    void cancelJob(UUID uuid);

    /**
     * Cancels jobs.
     *
     * @param ids ids of Jobs
     */
    void cancelJobs(Id... ids);

    /**
     * Cancels all jobs which scheduled in old versions of app.
     */
    void cancelOldJobs();

    /**
     * Gets state whether or not job is pending.
     *
     * @param id id of job
     * @return state of job
     */
    boolean isJobPending(Id id);
}
