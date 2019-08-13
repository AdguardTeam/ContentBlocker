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
import com.adguard.android.contentblocker.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.WeakHashMap;

/**
 * Service locator class.
 */
public class ServiceLocator {
    private final Context context;
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLocator.class);
    private static WeakHashMap<Context, ServiceLocator> locators = new WeakHashMap<>();

    private FilterService filterService;
    private PreferencesService preferencesService;
    private NotificationService notificationService;
    private RateService rateService;
    private DbHelper dbHelper;

    /**
     * Creates an instance of the ServiceLocator
     *
     * @param context Context
     */
    private ServiceLocator(Context context) {
        LOG.info("Initializing ServiceLocator for {}", context);
        this.context = context;
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
     * @return Filter service singleton
     */
    public FilterService getFilterService() {
        if (filterService == null) {
            filterService = new FilterServiceImpl(context, getDbHelper());
        }

        return filterService;
    }

    /**
     * @return Preferences service singleton
     */
    public PreferencesService getPreferencesService() {
        if (preferencesService == null) {
            preferencesService = new PreferencesServiceImpl(context);
        }

        return preferencesService;
    }

    /**
     * @return notifications service singleton
     */
    public NotificationService getNotificationService() {
        if (notificationService == null) {
            notificationService = new NotificationServiceImpl(context);
        }

        return notificationService;
    }

    /**
     * @return Data base helper
     */
    private DbHelper getDbHelper() {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context);
        }

        return dbHelper;
    }

    public RateService getRateService() {
        if (rateService == null) {
            rateService = new RateServiceImpl(context);
        }
        return rateService;
    }
}
