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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adguard.android.ServiceLocator;
import com.adguard.android.commons.BrowserUtils;
import com.adguard.android.contentblocker.onboarding.OnboardingActivity;
import com.adguard.android.model.FilterList;
import com.adguard.android.service.FilterService;
import com.adguard.android.service.FilterServiceImpl;
import com.adguard.android.service.PreferencesService;
import com.adguard.android.ui.utils.ActivityUtils;

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

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // As we're using a Toolbar, we should retrieve it and set it to be our ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        leftDrawer = (LinearLayout) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        DrawerArrayAdapter<DrawerListItem> adapter = new DrawerArrayAdapter<>(this, R.layout.drawer_list_item, R.id.text_view, R.id.image_view, getDrawerItems());
        drawerList.setAdapter(adapter);
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerLayout.setDrawerListener(this);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openned_drawer_title, R.string.closed_drawer_title);
        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);

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

        PreferencesService preferencesService = ServiceLocator.getInstance(getApplicationContext()).getPreferencesService();
        //if (!preferencesService.isOnboardingShown()) {
            startActivity(new Intent(this, OnboardingActivity.class));
        //}
    }

    private DrawerListItem[] getDrawerItems() {
        String[] menuTitles = getResources().getStringArray(R.array.menu_titles);
        int[] images = {
                R.drawable.ic_settings_black,
                0,
                // 0, // Whitelist is unimplemented now
                R.drawable.ic_sync_black_24dp,
                R.drawable.ic_stars_black,
                R.drawable.ic_info_black_24dp,
                R.drawable.ic_exit
        };
        DrawerListItem[] items = new DrawerListItem[menuTitles.length];
        for (int i = 0; i < menuTitles.length; i++) {
            items[i] = new DrawerListItem(menuTitles[i], images[i]);
        }
        return items;
    }

    @SuppressWarnings("ConstantConditions")
    private void refreshMainInfo() {
        boolean available = false;
        boolean reorder = false;

        boolean samsungBrowserAvailable = BrowserUtils.isSamsungBrowserAvailable(this);
        boolean yandexBrowserAvailable = BrowserUtils.isYandexBrowserAvailable(this);

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
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setComponent(new ComponentName("com.sec.android.app.sbrowser", "com.sec.android.app.sbrowser.SBrowserMainActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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
        }

        if (yandexBrowserAvailable) {
            available = true;
            findViewById(R.id.start_yandex_browser).setVisibility(View.VISIBLE);
            findViewById(R.id.start_yandex_settings).setVisibility(View.VISIBLE);
            findViewById(R.id.install_yandex_browser).setVisibility(View.GONE);
            findViewById(R.id.start_yandex_browser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setComponent(new ComponentName("com.yandex.browser", "com.yandex.browser.YandexBrowserMainActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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
        }

        if (available) {
            findViewById(R.id.yandex_card).setVisibility(View.VISIBLE);
            findViewById(R.id.samsung_card).setVisibility(View.VISIBLE);
            findViewById(R.id.no_browsers_card).setVisibility(View.GONE);
            if (reorder) {
                View yandex = findViewById(R.id.yandex_card);
                LinearLayout layout = (LinearLayout) findViewById(R.id.cards_layout);
                layout.removeView(yandex);
                layout.addView(yandex);
            }
        } else {
            findViewById(R.id.yandex_card).setVisibility(View.GONE);
            findViewById(R.id.samsung_card).setVisibility(View.GONE);
            findViewById(R.id.no_browsers_card).setVisibility(View.VISIBLE);

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

    private void openFiltersSettings() {
        Intent intent = new Intent(MainActivity.this, FiltersActivity.class);
        startActivity(intent);
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
                    startActivity(new Intent(MainActivity.this, FiltersActivity.class));
                    break;
                case 2:
                    drawerLayout.closeDrawers();
                    refreshStatus();
                    break;
                case 3:
                    drawerLayout.closeDrawers();
                    ActivityUtils.startMarket(MainActivity.this, getPackageName(), "rate_menu_item");
                    break;
                case 4:
                    drawerLayout.closeDrawers();
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    break;
                default:
                    finish();
                    break;
            }
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
                    refreshStatus();
                    return true;
            }

            return false;
        }
    }

    private class DrawerListItem {
        String text;
        int imageResourceId;

        public DrawerListItem(String text, int imageResourceId) {
            this.text = text;
            this.imageResourceId = imageResourceId;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private class DrawerArrayAdapter<T> extends ArrayAdapter<T> {

        private int imageFieldId = 0;

        public DrawerArrayAdapter(Context context, int resource) {
            super(context, resource);
        }

        public DrawerArrayAdapter(Context context, int resource, int textViewResourceId, int imageFieldId) {
            super(context, resource, textViewResourceId);
            this.imageFieldId = imageFieldId;
        }

        public DrawerArrayAdapter(Context context, int resource, T[] objects) {
            super(context, resource, objects);
        }

        public DrawerArrayAdapter(Context context, int resource, int textViewResourceId, int imageFieldId, T[] objects) {
            super(context, resource, textViewResourceId, objects);
            this.imageFieldId = imageFieldId;
        }

        public DrawerArrayAdapter(Context context, int resource, List<T> objects) {
            super(context, resource, objects);
        }

        public DrawerArrayAdapter(Context context, int resource, int textViewResourceId, int imageFieldId, List<T> objects) {
            super(context, resource, textViewResourceId, objects);
            this.imageFieldId = imageFieldId;
        }

        /**
         * {@inheritDoc}
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            if (imageFieldId != 0) {
                ImageView imageView = (ImageView) view.findViewById(imageFieldId);
                T item = getItem(position);
                if (item instanceof DrawerListItem) {
                    imageView.setImageResource(((DrawerListItem)item).imageResourceId);
                }
            }

            return view;
        }
    }
}
