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
package com.adguard.android.contentblocker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adguard.android.ServiceLocator;
import com.adguard.android.model.FilterList;
import com.adguard.android.service.FilterService;
import com.adguard.android.service.FilterServiceImpl;
import com.adguard.android.service.PreferencesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String YANDEX = "yandex";
    private static Logger LOG = LoggerFactory.getLogger(MainActivity.class);
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private LinearLayout leftDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isYandexBrowserAvailable() && !isSamsungBrowserAvailable()) {
            Intent intent = new Intent(this, NoBrowsersFoundActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        }

        // As we're using a Toolbar, we should retrieve it and set it to be our ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] menuTitles = getResources().getStringArray(R.array.menu_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        leftDrawer = (LinearLayout) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, R.id.textView, menuTitles));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerLayout.setDrawerListener(this);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openned_drawer_title, R.string.closed_drawer_title);
        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void refreshMainInfo() {
        boolean available = false;

        if (isSamsungBrowserAvailable()) {
            available = true;
            findViewById(R.id.start_samsung_browser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSamsungBlockingOptions();
                }
            });
        } else {
            findViewById(R.id.start_samsung_browser).setVisibility(View.GONE);
        }

        if (isYandexBrowserAvailable()) {
            available = true;
            findViewById(R.id.start_yandex_browser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openYandexBlockingOptions();
                }
            });
        } else {
            findViewById(R.id.start_yandex_browser).setVisibility(View.GONE);
        }

        if (!available) {
            findViewById(R.id.start_browsers_card).setVisibility(View.GONE);
        }

        refreshStatistics();

        FilterServiceImpl.enableYandexContentBlocker(this, true);
        FilterServiceImpl.enableSamsungContentBlocker(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerVisible(leftDrawer);
        menu.findItem(R.id.refresh).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshStatus();
                return true;
            default:
                LOG.warn("ItemId = {}", item.getItemId());
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        refreshMainInfo();
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(leftDrawer)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private void refreshStatus() {
        ServiceLocator.getInstance(getApplicationContext()).getFilterService().checkFiltersUpdates(this);
    }

    private void refreshStatistics() {
        PreferencesService preferencesService = ServiceLocator.getInstance(this).getPreferencesService();
        final FilterService filterService = ServiceLocator.getInstance(this).getFilterService();

        Date now = preferencesService.getLastUpdateCheck();
        // Last update time
        if (now == null) {
            List<FilterList> filters = filterService.getFilters();
            for (FilterList filter : filters) {
                if (now == null) {
                    now = filter.getTimeUpdated();
                    LOG.info("Using first time of {}", filter.getName());
                } else {
                    if (filter.getTimeUpdated().after(now)) {
                        now = filter.getTimeUpdated();
                        LOG.info("Using time of {}", filter.getName());
                    }
                }
            }
            if (now == null) {
                now = new Date();
            }
        }

        String dateTime = DateUtils.formatDateTime(this, now.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_TIME);
        ((TextView) findViewById(R.id.updateTimeTextView)).setText(dateTime);

        // Enabled filters count
        int filterListCount = filterService.getEnabledFilterListCount();
        ((TextView) findViewById(R.id.filtersCountTextView)).setText(String.format("%d", filterListCount));

        // Filter rules count
        int filterRuleCount = filterService.getFilterRuleCount();
        ((TextView) findViewById(R.id.rulesCountTextView)).setText(String.format("%d", filterRuleCount));

        if (filterRuleCount == 0) {
            new ApplyAndRefreshTask(filterService, this).execute();
        }
    }

    public static class ApplyAndRefreshTask extends AsyncTask<Void, Void, Integer> {

        private final FilterService service;
        private final Activity activity;

        public ApplyAndRefreshTask(FilterService service, Activity activity) {
            this.service = service;
            this.activity = activity;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            service.applyNewSettings();
            return service.getFilterRuleCount();
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(Integer filterRuleCount) {
            ((TextView) activity.findViewById(R.id.rulesCountTextView)).setText(String.format("%d", filterRuleCount));
        }
    }

    public void openSamsungBlockingOptions() {
        Intent intent = new Intent();
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_SETTING");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            startActivity(intent);
        }
    }

    public boolean isSamsungBrowserAvailable() {
        Intent intent = new Intent();
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_SETTING");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list)
            {
                if (info.activityInfo.packageName.contains("com.sec.") || info.activityInfo.packageName.contains("samsung")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void openYandexBlockingOptions() {
        Intent intent = new Intent();
        intent.setAction("com.yandex.browser.contentBlocker.ACTION_SETTING");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            startActivity(intent);
            return;
        }

        // For samsung-type action in Yandex browser
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_SETTING");
        list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            boolean found = false;
            for (ResolveInfo info : list)
            {
                if (info.activityInfo.packageName.contains(YANDEX)) {
                    found = true;
                }
            }
            if (found) {
                startActivity(intent);
            }
        }
    }

    public boolean isYandexBrowserAvailable() {
        Intent intent = new Intent();
        intent.setAction("com.yandex.browser.contentBlocker.ACTION_SETTING");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list)
            {
                if (info.resolvePackageName.contains(YANDEX)) {
                    return true;
                }
            }
        }

        // For samsung-type action in Yandex browser
        intent.setAction("com.samsung.android.sbrowser.contentBlocker.ACTION_SETTING");
        list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            for (ResolveInfo info : list)
            {
                if (info.activityInfo.packageName.contains(YANDEX)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        // TODO make it better
        if (slideOffset > 0.5f && slideOffset < 0.7f) {
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        refreshStatistics();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    drawerLayout.closeDrawers();
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    break;
                case 1:
                    drawerLayout.closeDrawers();
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    break;
                case 2:
                    finish();
                    break;
            }
        }
    }
}
