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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.commons.BrowserUtils;
import com.adguard.android.contentblocker.model.FilterList;
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

    private String[] reportTypeList;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reportTypeList = new String[] {
                getResources().getString(R.string.reportTypeMissedAd),
                getResources().getString(R.string.reportTypeIncorrectBlocking),
                getResources().getString(R.string.reportTypeBugReport),
                getResources().getString(R.string.reportTypeFeatureRequest),
                getResources().getString(R.string.reportTypeCustom),
        };

        // As we're using a Toolbar, we should retrieve it and set it to be our ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

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
        //drawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);

        findViewById(R.id.go_to_filters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFiltersSettings();
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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        final PreferencesService preferencesService = ServiceLocator.getInstance(getApplicationContext()).getPreferencesService();
        if (!preferencesService.isOnboardingShown()) {
            NavigationHelper.redirectToActivity(this, OnboardingActivity.class);
        }

        if (!preferencesService.isWelcomeMessage()) {
            final View bottomBarView = findViewById(R.id.bottom_bar);
            bottomBarView.setVisibility(View.VISIBLE);

            bottomBarView.findViewById(R.id.no_thanks).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preferencesService.setWelcomeMessage(true);
                    bottomBarView.setVisibility(View.GONE);
                }
            });

            bottomBarView.findViewById(R.id.learn_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preferencesService.setWelcomeMessage(true);
                    bottomBarView.setVisibility(View.GONE);

                    NavigationHelper.redirectToWebSite(MainActivity.this, "http://agrd.io/cb_adguard_products");
                }
            });
        }

        ServiceLocator.getInstance(getApplicationContext()).getFilterService().scheduleFiltersUpdate();
    }

    @SuppressWarnings("ConstantConditions")
    private void refreshMainInfo() {
        boolean available = false;
        boolean reorder = false;

        final boolean samsungBrowserAvailable = BrowserUtils.isSamsungBrowserAvailable(this);
        final boolean yandexBrowserAvailable = BrowserUtils.isYandexBrowserAvailable(this);

        if (samsungBrowserAvailable) {
            available = true;
            if (!yandexBrowserAvailable) {
                reorder = true;
            }
            findViewById(R.id.start_samsung_browser).setVisibility(View.VISIBLE);
            findViewById(R.id.start_samsung_settings).setVisibility(View.VISIBLE);
            findViewById(R.id.install_samsung_browser).setVisibility(View.GONE);
            findViewById(R.id.start_samsung_browser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowserUtils.startSamsungBrowser(MainActivity.this);
                }
            });
            findViewById(R.id.start_samsung_settings).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowserUtils.openSamsungBlockingOptions(MainActivity.this);
                }
            });
        } else {
            findViewById(R.id.start_samsung_browser).setVisibility(View.GONE);
            findViewById(R.id.start_samsung_settings).setVisibility(View.GONE);
            findViewById(R.id.install_samsung_browser).setVisibility(View.VISIBLE);
            findViewById(R.id.install_samsung_browser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.startMarket(MainActivity.this, "com.sec.android.app.sbrowser", null);
                }
            });
        }

        if (yandexBrowserAvailable) {
            available = true;
            findViewById(R.id.start_yandex_browser).setVisibility(View.VISIBLE);
            findViewById(R.id.start_yandex_settings).setVisibility(View.VISIBLE);
            findViewById(R.id.install_yandex_browser).setVisibility(View.GONE);
            findViewById(R.id.start_yandex_browser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowserUtils.startYandexBrowser(MainActivity.this);
                }
            });
            findViewById(R.id.start_yandex_settings).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowserUtils.openYandexBlockingOptions(MainActivity.this);
                }
            });
        } else {
            findViewById(R.id.start_yandex_browser).setVisibility(View.GONE);
            findViewById(R.id.start_yandex_settings).setVisibility(View.GONE);
            findViewById(R.id.install_yandex_browser).setVisibility(View.VISIBLE);
            findViewById(R.id.install_yandex_browser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.startMarket(MainActivity.this, "com.yandex.browser", "adguard1");
                }
            });
        }

        if (available) {
            findViewById(R.id.choose_browser_button).setVisibility(View.GONE);
            findViewById(R.id.enable_adguard_button).setVisibility(View.VISIBLE);

            findViewById(R.id.enable_adguard_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (yandexBrowserAvailable) {
                        BrowserUtils.openYandexBlockingOptions(MainActivity.this);
                    } else {
                        BrowserUtils.openSamsungBlockingOptions(MainActivity.this);
                    }
                }
            });

            if (reorder) {
                View yandex = findViewById(R.id.yandex_card);
                LinearLayout layout = findViewById(R.id.cards_layout);
                layout.removeView(yandex);
                layout.addView(yandex);
            }

            PreferencesService preferencesService = ServiceLocator.getInstance(getApplicationContext()).getPreferencesService();
            if (preferencesService.getBrowserConnectedCount() > 0) {
                View noBrowsersCard = findViewById(R.id.no_browsers_card);
                LinearLayout layout = findViewById(R.id.cards_layout);
                layout.removeView(noBrowsersCard);
                layout.addView(noBrowsersCard);
            }
        } else {
            findViewById(R.id.choose_browser_button).setVisibility(View.VISIBLE);
            findViewById(R.id.enable_adguard_button).setVisibility(View.GONE);

            findViewById(R.id.choose_browser_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowserUtils.showBrowserInstallDialog(MainActivity.this);
                }
            });
        }

        refreshStatistics();

        FilterServiceImpl.enableContentBlocker(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //drawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
        // drawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
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
                updateFilters();
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

    private void updateFilters() {
        ServiceLocator.getInstance(getApplicationContext()).getFilterService().checkFiltersUpdates(this);
    }

    @SuppressLint("DefaultLocale")
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

    private void openFiltersSettings() {
        NavigationHelper.redirectToActivity(MainActivity.this, FiltersActivity.class);
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
                updateFilters();
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
                NavigationHelper.redirectToWebSite(MainActivity.this, "https://github.com/AdguardTeam/ContentBlocker");
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


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.report_dialog_title);
        builder.setSingleChoiceItems(reportTypeList, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int selectedIndex) {
                dialogInterface.dismiss();

                if (selectedIndex == 0 || selectedIndex == 1) {
                    NavigationHelper.redirectToWebSite(MainActivity.this, ReportToolUtils.getUrl(MainActivity.this));
                } else {
                    NavigationHelper.redirectToWebSite(MainActivity.this, "https://github.com/AdguardTeam/ContentBlocker/issues/new");
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
                    openFiltersSettings();
                    return true;
                case R.id.check_filter_updates:
                    updateFilters();
                    return true;
            }

            return false;
        }
    }
}
