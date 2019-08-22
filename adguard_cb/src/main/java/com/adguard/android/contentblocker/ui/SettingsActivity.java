/*
 * This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 * Copyright © 2018 AdGuard Content Blocker. All rights reserved.
 * <p>
 * AdGuard Content Blocker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * <p>
 * AdGuard Content Blocker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.contentblocker.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.service.FilterService;
import com.adguard.android.contentblocker.service.PreferencesService;
import com.adguard.android.contentblocker.ui.utils.NavigationHelper;
import com.adguard.android.contentblocker.ui.utils.ProgressDialogUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final PreferencesService preferencesService = ServiceLocator.getInstance(getApplicationContext()).getPreferencesService();
        final FilterService filterService = ServiceLocator.getInstance(getApplicationContext()).getFilterService();

        final CheckBox autoUpdateView = findViewById(R.id.auto_update_checkbox);
        autoUpdateView.setChecked(preferencesService.isAutoUpdateFilters());
        autoUpdateView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                preferencesService.setAutoUpdateFilters(enable);
            }
        });

        findViewById(R.id.auto_update_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoUpdateView.setChecked(!autoUpdateView.isChecked());
            }
        });

        final CheckBox updateWifiOnlyView = findViewById(R.id.update_wifi_only_checkbox);
        updateWifiOnlyView.setChecked(preferencesService.isUpdateOverWifiOnly());
        updateWifiOnlyView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                preferencesService.setUpdateOverWifiOnly(enable);
            }
        });

        findViewById(R.id.update_wifi_only_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateWifiOnlyView.setChecked(!updateWifiOnlyView.isChecked());
            }
        });

        final CheckBox showUsefulAdsView = findViewById(R.id.show_useful_ads_checkbox);
        showUsefulAdsView.setChecked(filterService.isShowUsefulAds());
        showUsefulAdsView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
                filterService.setShowUsefulAds(enable);
            }
        });

        findViewById(R.id.show_useful_ads_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUsefulAdsView.setChecked(!showUsefulAdsView.isChecked());
            }
        });

        findViewById(R.id.filter_list_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationHelper.redirectToActivity(SettingsActivity.this, FiltersActivity.class);
            }
        });

        findViewById(R.id.clear_filter_cache_wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = ProgressDialogUtils.showProgressDialog(SettingsActivity.this,
                        R.string.please_wait,
                        R.string.clear_filters_cache_progress_message);

                filterService.clearCacheAndUpdateFilters(progressDialog);
            }
        });
    }
}
