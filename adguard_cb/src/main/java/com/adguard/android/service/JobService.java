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
