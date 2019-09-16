package com.adguard.android.contentblocker.service.job;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkRequest;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Interface for classes which want to be able to run scheduled jobs.
 *
 * <b>FOR DEVELOPERS</b>
 * If you want to use one time jobs, you have to create another two interfaces with names PeriodicJob and OneTimeJob.
 * Also you have to move methods {@link #getPeriodicInterval()} and {@link #getFlexInterval()} to PeriodicJob interface.</pre>
 */
public interface Job {

    /**
     * Runs job when time for it will come.
     *
     * @return job result (success or not). If not, we'll use the backoff policy to retry the job.
     */
    boolean run();

    /**
     * Creates an instance of {@link WorkRequest.Builder} which contains conditions of job's running.
     *
     * @return instance of {@link WorkRequest.Builder}
     */
    @NonNull WorkRequest.Builder createWorkRequestBuilder();

    /**
     * Gets state whether or not job can schedule right now.
     *
     * @return state
     */
    default boolean canSchedule() {
        return true;
    }

    /**
     * Gets id of job.
     *
     * @return instance of {@link Id}
     */
    @NonNull Id getId();

    /**
     * <pre>
     * Gets periodic interval in millis for scheduling of job.
     * Should be overridden if job will be periodic.
     *
     * See more in {@link androidx.work.PeriodicWorkRequest.Builder#Builder(Class, long, TimeUnit, long, TimeUnit)} description.</pre>
     *
     * @return periodic interval in millis, must be greater than or equal to {@link androidx.work.PeriodicWorkRequest#MIN_PERIODIC_INTERVAL_MILLIS}
     */
    @IntRange(from = PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS)
    long getPeriodicInterval();

    /**
     * <pre>
     * Gets flex interval in millis for which this job repeats from the end of the {@link #getPeriodicInterval()}.
     * Should be overridden if job will be periodic.
     * Ignored for certain OS versions (in particular, API 23).
     *
     * See more in {@link androidx.work.PeriodicWorkRequest.Builder#Builder(Class, long, TimeUnit, long, TimeUnit)} description.</pre>
     *
     * @return flex interval in millis, must be greater than or equal to {@link androidx.work.PeriodicWorkRequest#MIN_PERIODIC_FLEX_MILLIS}
     */
    @IntRange(from = PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS)
    long getFlexInterval();
}
