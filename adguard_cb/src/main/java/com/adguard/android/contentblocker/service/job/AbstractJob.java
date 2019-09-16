package com.adguard.android.contentblocker.service.job;

import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkRequest;

import com.adguard.android.contentblocker.commons.function.BooleanSupplier;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Abstract job implementation for later use in other classes.
 *
 * <b>FOR DEVELOPERS</b>
 * This is implementation for periodic jobs.
 * If you want to implement one time job, you have to implement another AbstractOneTimeJob class and rename this class.</pre>
 */
abstract class AbstractJob implements Job {

    /**
     * For our backoff policy we're using 10 minutes as the start value
     * Which is then increased according to the linear policy.
     */
    private static final long DEFAULT_BACKOFF_PERIOD = TimeUnit.MINUTES.toSeconds(10);

    private Id id;
    private BooleanSupplier jobRunner;
    private BooleanSupplier canScheduleRunner;
    private long periodicInterval;
    private long flexInterval;
    private long backoffPeriod;

    AbstractJob(Id id, BooleanSupplier jobRunner, long periodicInterval) {
        this(id, jobRunner, null, periodicInterval, DEFAULT_BACKOFF_PERIOD);
    }

    AbstractJob(Id id, BooleanSupplier jobRunner, BooleanSupplier canScheduleRunner, long periodicInterval, long backoffPeriod) {
        this(id, jobRunner, canScheduleRunner, periodicInterval, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, backoffPeriod);
    }

    private AbstractJob(Id id, BooleanSupplier jobRunner, BooleanSupplier canScheduleRunner, long periodicInterval, long flexInterval, long backoffPeriod) {
        this.id = id;
        this.jobRunner = jobRunner;
        this.canScheduleRunner = canScheduleRunner;
        this.periodicInterval = periodicInterval;
        this.flexInterval = flexInterval;
        this.backoffPeriod = backoffPeriod;
    }

    @Override
    public boolean run() {
        if (jobRunner != null) {
            return jobRunner.get();
        }
        // No job runner -- count this as success
        return true;
    }

    @NonNull
    @Override
    public WorkRequest.Builder createWorkRequestBuilder() {
        Constraints.Builder constraintsBuilder = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED);

        // Please note, that we do not use "setRequiresDeviceIdle" here for a reason.
        // It appears that this method makes our jobs highly unreliable due to unclear
        // definition of "idleness". So instead of this we use a custom method: "DeviceState.isIdle".

        return new PeriodicWorkRequest.Builder(Worker.class,
                getPeriodicInterval(), TimeUnit.MILLISECONDS, getFlexInterval(), TimeUnit.MILLISECONDS)
                .setConstraints(constraintsBuilder.build())
                .setBackoffCriteria(BackoffPolicy.LINEAR, backoffPeriod, TimeUnit.SECONDS)
                .addTag(getId().getTag());
    }

    @Override
    public boolean canSchedule() {
        return canScheduleRunner == null || canScheduleRunner.get();
    }

    @NonNull
    @Override
    public Id getId() {
        return id;
    }

    @Override
    public long getPeriodicInterval() {
        return periodicInterval;
    }

    @Override
    public long getFlexInterval() {
        return flexInterval;
    }
}
