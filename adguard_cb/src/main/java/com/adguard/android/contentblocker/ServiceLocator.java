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
package com.adguard.android.contentblocker;

import android.content.Context;

import com.adguard.android.contentblocker.db.DbHelper;
import com.adguard.android.contentblocker.service.FilterService;
import com.adguard.android.contentblocker.service.FilterServiceImpl;
import com.adguard.android.contentblocker.service.NotificationService;
import com.adguard.android.contentblocker.service.NotificationServiceImpl;
import com.adguard.android.contentblocker.service.PreferencesService;
import com.adguard.android.contentblocker.service.PreferencesServiceImpl;
import com.adguard.android.contentblocker.service.job.Id;
import com.adguard.android.contentblocker.service.job.JobService;
import com.adguard.android.contentblocker.service.job.JobServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.WeakHashMap;

/**
 * Service locator class.
 */
public class ServiceLocator {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLocator.class);
    private static WeakHashMap<Context, ServiceLocator> locators = new WeakHashMap<>();

    private FilterService filterService;
    private PreferencesService preferencesService;
    private NotificationService notificationService;
    private JobService jobService;

    /**
     * Creates an instance of the ServiceLocator
     *
     * @param context Context
     */
    private ServiceLocator(Context context) {
        LOG.info("Initializing ServiceLocator for {}", context);
        preferencesService = new PreferencesServiceImpl(context);
        notificationService = new NotificationServiceImpl(context);
        filterService = new FilterServiceImpl(context, new DbHelper(context), preferencesService, notificationService);
        jobService = new JobServiceImpl(this);

        LOG.info("ServiceLocator setup...");
        checkFirstLaunch();
        jobService.cancelOldJobs();
        jobService.scheduleJobs(Id.FILTERS, Id.RATE_NOTIFICATION);
    }

    /**
     * Gets service locator instance
     *
     * @param context Context
     * @return ServiceLocator instance
     */
    public synchronized static ServiceLocator getInstance(Context context) {
        Context applicationContext = context.getApplicationContext();
        if (applicationContext == null) {
            applicationContext = context;
        }
        ServiceLocator instance = locators.get(applicationContext);

        if (instance == null) {
            instance = new ServiceLocator(applicationContext);
            locators.put(applicationContext, instance);
        }

        return instance;
    }

    /**
     * @return Filter service reference
     */
    public FilterService getFilterService() {
        return filterService;
    }

    /**
     * @return Preferences service reference
     */
    public PreferencesService getPreferencesService() {
        return preferencesService;
    }

    /**
     * @return notifications service reference
     */
    public NotificationService getNotificationService() {
        return notificationService;
    }

    /**
     * @return job service reference
     */
    public JobService getJobService() {
        return jobService;
    }

    private void checkFirstLaunch() {
        if (preferencesService.getInstallationTime() == 0L) {
            // It's first launch. We need to set installation time to current
            preferencesService.setInstallationTime(System.currentTimeMillis());
        }
    }
}
