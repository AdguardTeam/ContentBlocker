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
