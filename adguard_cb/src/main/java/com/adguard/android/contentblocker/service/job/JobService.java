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
