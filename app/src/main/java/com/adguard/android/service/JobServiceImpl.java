/**
 This file is part of Adguard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2016 Performix LLC. All rights reserved.

 Adguard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 Adguard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 Adguard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
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
