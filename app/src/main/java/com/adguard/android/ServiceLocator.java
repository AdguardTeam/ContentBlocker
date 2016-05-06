package com.adguard.android;

import android.content.Context;
import com.adguard.android.service.*;
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
    private JobService jobService;

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
        ServiceLocator instance = locators.get(context.getApplicationContext());

        if (instance == null) {
            instance = new ServiceLocator(context);
            locators.put(context, instance);
        }

        return instance;
    }

    /**
     * @return Filter service singleton
     */
    public FilterService getFilterService() {
        if (filterService == null) {
            filterService = new FilterServiceImpl(context);
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
     * @return Job service singleton
     */
    public JobService getJobService() {
        if (jobService == null) {
            jobService = new JobServiceImpl();
        }

        return jobService;
    }
}
