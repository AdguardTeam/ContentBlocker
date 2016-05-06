package com.adguard.android.service;

import com.adguard.commons.concurrent.ExecutorsPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Job service implementation using ScheduledExecutorService
 */
public class JobServiceImpl implements JobService {

    private final Logger LOG = LoggerFactory.getLogger(JobServiceImpl.class);

    /**
     * Creates an instance of JobService
     */
    public JobServiceImpl() {
        LOG.info("Initializing JobService");
    }

    @Override
    public void scheduleAtFixedRate(String jobName, Runnable command, long initialDelay, long period, TimeUnit timeUnit) {
        LOG.debug("Schedule job {} with delay={} and period={} ({})", jobName, initialDelay, period, timeUnit);
        Job job = new Job(jobName, command);
        ExecutorsPool.getSingleThreadScheduledExecutorService().scheduleAtFixedRate(job, initialDelay, period, timeUnit);
    }

	private static class Job implements Runnable {

        private final Logger LOG = LoggerFactory.getLogger(JobServiceImpl.class);
        private final String jobName;
        private final Runnable command;

        /**
         * Creates job instance
         *
         * @param jobName Job name
         * @param command Job command
         */
        public Job(String jobName, Runnable command) {
            this.jobName = jobName;
            this.command = command;
        }

        @Override
        public void run() {
            LOG.info("Start executing job {}", jobName);

            try {
                command.run();
                LOG.info("Finished executing job {}", jobName);
            } catch (Exception ex) {
                LOG.error("Error while executing job {}:\r\n{}", jobName, ex);
            }
        }
    }
}
