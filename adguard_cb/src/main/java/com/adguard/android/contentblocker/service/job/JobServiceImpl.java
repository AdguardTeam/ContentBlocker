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

import androidx.annotation.NonNull;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.adguard.android.contentblocker.BuildConfig;
import com.adguard.android.contentblocker.ServiceLocator;
import com.google.common.util.concurrent.ListenableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of {@link JobService}.
 */
public class JobServiceImpl implements JobService {
    private static final Logger LOG = LoggerFactory.getLogger(JobServiceImpl.class);

    private WeakReference<ServiceLocator> serviceLocatorRef;
    private WorkManager workManager;

    public JobServiceImpl(ServiceLocator serviceLocator) {
        this.serviceLocatorRef = new WeakReference<>(serviceLocator);
        this.workManager = WorkManager.getInstance();
    }

    @Override
    public void scheduleJobs(Id... ids) {
        ServiceLocator serviceLocator = serviceLocatorRef.get();
        if (ids == null || serviceLocator == null) {
            return;
        }
        String versionTag = BuildConfig.VERSION_NAME;
        for (Id id : ids) {
            Job job = JobFactory.getJob(serviceLocator, id);
            if (job == null) {
                LOG.warn("Job {} doesn't exist.", id);
                continue;
            }
            if (id != Id.UNKNOWN && !isJobPending(id) && canSchedule(job)) {
                LOG.info("Scheduling job for ID {}...", id.getTag());
                workManager.enqueue(job.createWorkRequestBuilder().addTag(versionTag).build());
            }
        }
    }

    @Override
    public void cancelJob(UUID uuid) {
        if (uuid == null) {
            return;
        }
        workManager.cancelWorkById(uuid);
        workManager.pruneWork();
    }

    @Override
    public void cancelJobs(Id... ids) {
        if (ids == null) {
            return;
        }

        for (Id id : ids) {
            String tag = id.getTag();
            LOG.info("Cancelling job ID {}...", tag);
            workManager.cancelAllWorkByTag(tag);
            workManager.pruneWork();
        }
    }

    @Override
    public void cancelOldJobs() {
        ServiceLocator serviceLocator = serviceLocatorRef.get();
        if (serviceLocator == null) {
            return;
        }
        String versionTag = BuildConfig.VERSION_NAME;

        for (Id id : Id.values()) {
            deleteJobsWithoutTag(versionTag, workManager.getWorkInfosByTag(id.getTag()));
        }
    }

    @Override
    public boolean isJobPending(Id id) {
        try {
            return !workManager.getWorkInfosByTag(id.getTag()).get().isEmpty();
        } catch (ExecutionException | InterruptedException e) {
            LOG.warn("Error while checking whether job is pending or not", e);
            return false;
        }
    }

    private void deleteJobsWithoutTag(@NonNull String tag, @NonNull ListenableFuture<List<WorkInfo>> future) {
        try {
            List<UUID> uuids = new ArrayList<>();
            for (WorkInfo info : future.get()) {
                boolean containsTag = false;
                for (String jobTag : info.getTags()) {
                    if (jobTag.equals(tag)) {
                        containsTag = true;
                        break;
                    }
                }
                if (!containsTag) {
                    uuids.add(info.getId());
                }
            }
            for (UUID uuid : uuids) {
                LOG.info("Deleting old job {} as it has no tag {}", uuid, tag);
                workManager.cancelWorkById(uuid);
            }
            if (!uuids.isEmpty()) {
                workManager.pruneWork();
            }
        } catch (ExecutionException | InterruptedException e) {
            LOG.warn("Error while deleting jobs without tag", e);
        }
    }

    private boolean canSchedule(@NonNull Job job) {
        boolean state = job.canSchedule();
        LOG.info("Trying check job {} can schedule, state: {}", job.getId().getTag(), state);
        return state;
    }
}

