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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.adguard.android.contentblocker.ServiceLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that performs jobs scheduled by {@link JobService}.
 */
public class Worker extends androidx.work.Worker {

    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    public Worker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Id id = getIdByTag();
        Job job = JobFactory.getJob(getApplicationContext(), id);
        if (job != null) {
            LOG.info("Job with tag {} running...", id.getTag());
            boolean result = job.run();
            LOG.info("Job with tag {} result is {}", id.getTag(), result);
            if (!result) {
                // Default backoff policy will be used (30 sec, exponential).
                // See {@link WorkRequest#setBackoffCriteria} comment for
                // more details on it.
                return Result.retry();
            }
        } else {
            LOG.warn("Job was not found and will be canceled. Tags: {}. Resolved id: {}.", getTags(), id);
            ServiceLocator.getInstance(getApplicationContext()).getJobService().cancelJob(getId());
        }
        return Result.success();
    }

    private Id getIdByTag() {
        if (getTags().isEmpty()) {
            return Id.UNKNOWN;
        }

        /*
          WorkManager can add its own tags to track the task.
          We are looking for our own tag, which is unique and allows us to define a task
         */
        for (String tag : getTags()) {
            Id id = Id.valueOfTag(tag);
            if (id != Id.UNKNOWN) {
                return id;
            }
        }
        return Id.UNKNOWN;
    }
}
