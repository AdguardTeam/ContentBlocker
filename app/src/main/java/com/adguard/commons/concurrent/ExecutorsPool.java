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
package com.adguard.commons.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Pool for commonly used executor services.
 * Use this pool instead of creating ExecutorService every time
 */
public class ExecutorsPool {

    private static ExecutorService cachedExecutorService;
    private static ScheduledExecutorService singleThreadScheduledExecutorService;

    /**
     * @return Standard cached executor service.
     *         Core pool size equals to the number of available cores * 8.
     *         Max pool size is unlimited.
     *         Keep alive time is 60 seconds.
     */
    public static synchronized ExecutorService getCachedExecutorService() {
        if (cachedExecutorService == null) {
            cachedExecutorService = Executors.newCachedThreadPool();
        }

        return cachedExecutorService;
    }

    /**
     * @return Single-thread scheduled executor service singleton
     */
    public static synchronized ScheduledExecutorService getSingleThreadScheduledExecutorService() {
        if (singleThreadScheduledExecutorService == null) {
            singleThreadScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
        return singleThreadScheduledExecutorService;
    }
}
