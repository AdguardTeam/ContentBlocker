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
