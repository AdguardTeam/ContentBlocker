package com.adguard.android.service;

import java.util.concurrent.TimeUnit;

/**
 * Service that manages periodical tasks
 */
public interface JobService {

    /**
     * Schedules new job to be executed periodically
     *
     * @param jobName      Job name
     * @param command      Command to execute
     * @param initialDelay The time from now to delay execution
     * @param period       The time unit of the delay parameter
     * @param timeUnit     Time unit for initial delay and period
     */
    void scheduleAtFixedRate(String jobName, Runnable command, long initialDelay, long period, TimeUnit timeUnit);

}
