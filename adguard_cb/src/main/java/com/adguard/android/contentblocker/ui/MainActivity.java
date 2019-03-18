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
package com.adguard.android.contentblocker.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.commons.AppLink;
import com.adguard.android.contentblocker.commons.BrowserUtils;
import com.adguard.android.contentblocker.model.FilterList;
import com.adguard.android.contentblocker.model.ReportType;
import com.adguard.android.contentblocker.onboarding.OnboardingActivity;
import com.adguard.android.contentblocker.service.FilterService;
import com.adguard.android.contentblocker.service.FilterServiceImpl;
import com.adguard.android.contentblocker.service.PreferencesService;
import com.adguard.android.contentblocker.ui.utils.ActivityUtils;
import com.adguard.android.contentblocker.ui.utils.NavigationHelper;
import com.adguard.android.contentblocker.ui.utils.ReportToolUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener, SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {

    private static Logger LOG = LoggerFactory.getLogger(MainActivity.class);

    private DrawerLayout drawerLayout;
    private LinearLayout leftDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private PreferencesService preferencesService;
    private FilterService filterService;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filterService = ServiceLocator.getInstance(getApplicationContext()).getFilterService();
        preferencesService = ServiceLocator.getInstance(getApplicationContext()).getPreferencesService();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.empty);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        initDrawerLayout();

        findViewById(R.id.go_to_products).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = AppLink.Website.getOtherProductUrl(getApplicationContext(), "main_activity");
                NavigationHelper.redirectToWebSite(MainActivity.this, url);
            }
        });

        findViewById(R.id.go_to_filters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.redirectToActivity(MainActivity.this, FiltersActivity.class);
            }
        });

        final View menuImageView = findViewById(R.id.menuImageView);
        if (menuImageView != null) {
            menuImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // inflate menu
                    PopupMenu popup = new PopupMenu(menuImageView.getContext(), menuImageView);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.filters_popup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new FiltersMenuItemClickListener());
                    popup.show();
                }
            });
        }

        if (!preferencesService.isOnboardingShown()) {
            NavigationHelper.redirectToActivity(this, OnboardingActivity.class);
        }

        filterService.scheduleFiltersUpdate();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
                filterService.checkFiltersUpdates(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onResume() {
        super.onResume();
        refreshMainInfo();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(leftDrawer)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        leftDrawer = findViewById(R.id.left_drawer);

        findViewById(R.id.nav_filters).setOnClickListener(this);
        findViewById(R.id.nav_user_filter).setOnClickListener(this);
        findViewById(R.id.nav_whitelist).setOnClickListener(this);
        findViewById(R.id.nav_settings).setOnClickListener(this);
        findViewById(R.id.nav_check_filter_updates).setOnClickListener(this);
        findViewById(R.id.nav_report_bug).setOnClickListener(this);
        findViewById(R.id.nav_github).setOnClickListener(this);
        findViewById(R.id.nav_rate_app).setOnClickListener(this);
        findViewById(R.id.nav_about).setOnClickListener(this);
        findViewById(R.id.nav_exit).setOnClickListener(this);

        drawerLayout.addDrawerListener(this);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openned_drawer_title, R.string.closed_drawer_title);
    }

    private void refreshMainInfo() {
        boolean samsungBrowserAvailable = BrowserUtils.isSamsungBrowserAvailable(this);

        View settingAdguardInSumsung = findViewById(R.id.setting_adguard_samsung);
        settingAdguardInSumsung.setVisibility(samsungBrowserAvailable ? View.VISIBLE : View.GONE);
        settingAdguardInSumsung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowserUtils.openSamsungBlockingOptions(MainActivity.this);
            }
        });


        View installSamsungBrowser = findViewById(R.id.install_samsung_browser);
        installSamsungBrowser.setVisibility(samsungBrowserAvailable ? View.GONE : View.VISIBLE);
        installSamsungBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startMarket(MainActivity.this, BrowserUtils.SAMSUNG_BROWSER_PACKAGE, null);
            }
        });

        boolean yandexBrowserAvailable = BrowserUtils.isYandexBrowserAvailable(this);

        View settingAdgaurdInYandex = findViewById(R.id.setting_adguard_yandex);
        settingAdgaurdInYandex.setVisibility(yandexBrowserAvailable ? View.VISIBLE : View.GONE);
        settingAdgaurdInYandex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowserUtils.openYandexBlockingOptions(MainActivity.this);
            }
        });

        View installYandexBrowser = findViewById(R.id.install_yandex_browser);
        installYandexBrowser.setVisibility(yandexBrowserAvailable ? View.GONE : View.VISIBLE);
        installYandexBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startMarket(MainActivity.this, BrowserUtils.YANDEX_BROWSER_PACKAGE, "adguard1");
            }
        });

        refreshStatistics();
        FilterServiceImpl.enableContentBlocker(this);
    }

    @SuppressLint("DefaultLocale")
    private void refreshStatistics() {
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

        String dateTime = ActivityUtils.formatDateTime(this, now);
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

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        if (slideOffset > 0.5f && slideOffset < 0.7f) {
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (StringUtils.equalsIgnoreCase(key, PreferencesService.KEY_LAST_UPDATE_CHECK_DATE)) {
            refreshStatistics();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_settings:
                drawerLayout.closeDrawers();
                NavigationHelper.redirectToActivity(MainActivity.this, SettingsActivity.class);
                break;
            case R.id.nav_filters:
                drawerLayout.closeDrawers();
                NavigationHelper.redirectToActivity(MainActivity.this, FiltersActivity.class);
                break;
            case R.id.nav_user_filter:
                drawerLayout.closeDrawers();
                NavigationHelper.redirectToActivity(MainActivity.this, UserFilterActivity.class);
                break;
            case R.id.nav_whitelist:
                drawerLayout.closeDrawers();
                NavigationHelper.redirectToActivity(MainActivity.this, WhitelistActivity.class);
                break;
            case R.id.nav_check_filter_updates:
                drawerLayout.closeDrawers();
                filterService.checkFiltersUpdates(this);
                break;
            case R.id.nav_rate_app:
                drawerLayout.closeDrawers();
                ActivityUtils.startMarket(MainActivity.this, getPackageName(), "rate_menu_item");
                break;
            case R.id.nav_report_bug:
                drawerLayout.closeDrawers();
                showReportDialog();
                break;
            case R.id.nav_github:
                drawerLayout.closeDrawers();
                NavigationHelper.redirectToWebSite(MainActivity.this, AppLink.Github.getHomeUrl(getApplicationContext(), "main_activity"));
                break;
            case R.id.nav_about:
                drawerLayout.closeDrawers();
                NavigationHelper.redirectToActivity(MainActivity.this, AboutActivity.class);
                break;
            default:
                finish();
                break;
        }
    }

    private void showReportDialog() {
        final ArrayAdapter<ReportType> arrayAdapter = new ArrayAdapter<ReportType>(getApplicationContext(), R.layout.simple_dialog_item, ReportType.values()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                ReportType reportType = getItem(position);
                if (reportType != null) {
                    TextView textView = view.findViewById(R.id.text1);
                    textView.setText(reportType.getStringId());
                }

                return view;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.send_feedback_title);
        builder.setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                dialogInterface.dismiss();

                ReportType reportType = arrayAdapter.getItem(selectedIndex);
                if (reportType != null) {
                    switch (reportType) {
                        case MISSED_AD:
                        case INCORRECT_BLOCKING:
                            NavigationHelper.redirectToWebSite(MainActivity.this, ReportToolUtils.getUrl(MainActivity.this));
                            break;
                        default:
                            NavigationHelper.redirectToWebSite(MainActivity.this, AppLink.Github.getNewIssueUrl(getApplicationContext(), "main_activity"));
                            break;
                    }
                }
            }
        }).show();
    }

    private static class ApplyAndRefreshTask extends AsyncTask<Void, Void, Integer> {

        private final FilterService service;
        @SuppressLint("StaticFieldLeak")
        private final Activity activity;

        ApplyAndRefreshTask(FilterService service, Activity activity) {
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

    private class FiltersMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.go_to_filters:
                    NavigationHelper.redirectToActivity(MainActivity.this, FiltersActivity.class);
                    return true;
                case R.id.check_filter_updates:
                    filterService.checkFiltersUpdates(MainActivity.this);
                    return true;
            }

            return false;
        }
    }
}
