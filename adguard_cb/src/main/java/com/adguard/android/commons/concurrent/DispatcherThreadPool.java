/*
 This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2018 AdGuard Content Blocker. All rights reserved.

 AdGuard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 AdGuard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.commons.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * Thread pool that can dispatch tasks to different queues depending on the
 * queue name. Tasks from one queue will be handled one by one.
 */
public class DispatcherThreadPool {

    private static final String DEFAULT_QUEUE_NAME = "__DEFAULT__";
    private static Logger LOG = LoggerFactory.getLogger(DispatcherThreadPool.class);
    private static DispatcherThreadPool instance;

    /**
     * @return Default dispatcher thread pool
     */
    public static DispatcherThreadPool getInstance() {
        synchronized (DispatcherThreadPool.class) {
            if (instance == null) {
                instance = new DispatcherThreadPool();
            }
            return instance;
        }
    }

    private final Object syncRoot = new Object();
    private final Map<String, DispatcherTaskRunner> currentTasksMap = new HashMap<>();
    private final Map<String, Queue<DispatcherTask>> taskQueuesMap = new HashMap<>();
    private final ExecutorService executorService;

    /**
     * Creates an instance of the dispatcher thread pool
     * with cached thread pool as it's backend.
     */
    public DispatcherThreadPool() {
        this(ExecutorsPool.getCachedExecutorService());
    }

    /**
     * Creates an instance of the dispatcher thread pool
     * with the specified ExecutorService backing it up.
     *
     * @param executorService Executor service used as inner thread pool.
     */
    public DispatcherThreadPool(ExecutorService executorService) {
        this.executorService = executorService;
    }


    /**
     * Submits task to the default queue.
     *
     * @param dispatcherTask Task to submit
     */
    public void submit(DispatcherTask dispatcherTask) {
        submit(DEFAULT_QUEUE_NAME, dispatcherTask);
    }

    /**
     * Submits task to the specific queue
     *
     * @param queueName      Queue to handle the task
     * @param dispatcherTask Task to execute
     */
    public void submit(String queueName, DispatcherTask dispatcherTask) {
        synchronized (syncRoot) {
            DispatcherTaskRunner currentTaskRunner = currentTasksMap.get(queueName);
            if (currentTaskRunner == null) {
                currentTaskRunner = new DispatcherTaskRunner(queueName, dispatcherTask);
                currentTasksMap.put(queueName, currentTaskRunner);
                executorService.execute(currentTaskRunner);
            } else {
                enqueueNextTask(queueName, dispatcherTask);
            }
        }
    }

    /**
     * Clears default queue
     */
    public void clearQueue() {
        clearQueue(DEFAULT_QUEUE_NAME);
    }

    /**
     * Clears specified task queue
     *
     * @param queueName Queue name
     */
    public void clearQueue(String queueName) {
        synchronized (syncRoot) {
            taskQueuesMap.remove(queueName);
        }
    }

    /**
     * Gets length of the default queue
     *
     * @return Default queue length
     */
    public int getQueueLength() {
        return getQueueLength(DEFAULT_QUEUE_NAME);
    }

    /**
     * Gets specified queue length
     *
     * @param queueName Queue name
     * @return Length of the queue
     */
    public int getQueueLength(String queueName) {
        synchronized (syncRoot) {
            Queue<DispatcherTask> queue = taskQueuesMap.get(queueName);
            return queue == null ? 0 : queue.size();
        }
    }

    /**
     * Called when task has finished it's work
     *
     * @param queueName Queue name
     */
    private void onTaskFinished(String queueName) {
        synchronized (syncRoot) {
            currentTasksMap.remove(queueName);
            DispatcherTask nextTask = dequeueNextTask(queueName);

            if (nextTask != null) {
                submit(nextTask);
            }
        }
    }

    /**
     * Polls task from the queue
     *
     * @param queueName Queue name
     * @return Dispatcher task or null if queue is empty
     */
    private DispatcherTask dequeueNextTask(String queueName) {
        synchronized (syncRoot) {
            Queue<DispatcherTask> queue = taskQueuesMap.get(queueName);

            if (queue == null) {
                return null;
            }

            DispatcherTask dispatcherTask = queue.poll();
            if (queue.isEmpty()) {
                taskQueuesMap.remove(queueName);
            }
            return dispatcherTask;
        }
    }

    /**
     * Adds next task to be executed
     *
     * @param queueName      Queue name
     * @param dispatcherTask Dispatcher task
     */
    private void enqueueNextTask(String queueName, DispatcherTask dispatcherTask) {
        synchronized (syncRoot) {
            Queue<DispatcherTask> queue = taskQueuesMap.get(queueName);

            if (queue == null) {
                queue = new LinkedList<>();
                taskQueuesMap.put(queueName, queue);
            }

            queue.add(dispatcherTask);
        }
    }

    /**
     * Wraps dispatcher task so it could be executed.
     */
    private class DispatcherTaskRunner implements Runnable {

        private final DispatcherTask dispatcherTask;
        private final String queueName;

        /**
         * Creates an instance of the dispatcher task
         *
         * @param queueName      Dispatcher queue name
         * @param dispatcherTask Dispatcher task
         */
        public DispatcherTaskRunner(String queueName,
                                    DispatcherTask dispatcherTask) {
            this.queueName = queueName;
            this.dispatcherTask = dispatcherTask;
        }

        @Override
        public void run() {
            try {
                dispatcherTask.execute();
            } catch (Exception ex) {
                LOG.error("Error occurred while processing dispatcher task", ex);
            } finally {
                onTaskFinished(queueName);
            }
        }
    }
}
