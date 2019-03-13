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
package com.adguard.android.contentblocker.ui.utils;

import android.app.Activity;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.model.FilterList;
import com.adguard.android.contentblocker.service.FilterService;
import com.adguard.android.contentblocker.service.FilterServiceImpl;
import com.adguard.android.contentblocker.service.PreferencesService;

import java.util.Date;
import java.util.Locale;

/**
 *
 */
public class FilterViewAdapter extends BaseAdapter implements View.OnClickListener {

    private final Activity context;
    private final LayoutInflater layoutInflater;
    private final FilterService filterService;

    public FilterViewAdapter(Activity context, FilterService filterService) {
        this.context = context;
        this.filterService = filterService;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filterService.getFilterListCount();
    }

    @Override
    public Object getItem(int position) {
        return filterService.getFilters().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(layoutInflater, position, convertView, parent);
    }

    private View createViewFromResource(LayoutInflater inflater, int position, View convertView, ViewGroup parent) {
        FilterList filterList = filterService.getFilters().get(position);
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.filter_list_item, parent, false);
        } else {
            view = convertView;
        }

        ((TextView) view.findViewById(R.id.title)).setText(filterList.getName());
        CharSequence description = getFilterSummaryText(filterList);
        ((TextView) view.findViewById(R.id.summary)).setText(description);
        ((CheckBox) view.findViewById(R.id.checkbox)).setChecked(filterList.isEnabled());
        view.setOnClickListener(this);

        view.setTag(filterList);

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onClick(View v) {
        FilterList filterList = (FilterList) v.getTag();
        filterService.updateFilterEnabled(filterList, !filterList.isEnabled());
        ((CheckBox)v.findViewById(R.id.checkbox)).setChecked(filterList.isEnabled());

        if (filterList.getFilterId() == FilterServiceImpl.SHOW_USEFUL_ADS_FILTER_ID) {
            PreferencesService preferencesService = ServiceLocator.getInstance(context.getApplicationContext()).getPreferencesService();
            preferencesService.setShowUsefulAds(filterList.isEnabled());
        }
        new ApplyAndRefreshTask(filterService, context).execute();
    }

    private CharSequence getFilterSummaryText(FilterList filter) {
        StringBuilder sb = new StringBuilder();

        // Description
        sb.append(filter.getDescription());
        sb.append("\r\n");

        // Filter version
        sb.append(context.getString(R.string.filterVersionTemplate).replace("{0}", filter.getVersion().getLongVersionString()));

        // Updated time
        final Date updated = filter.getLastTimeDownloaded();
        if (updated != null && updated.getTime() > 0) {
            sb.append("\r\n");
            sb.append(context.getString(R.string.filterUpdatedTemplate)
                    .replace("{0}", ActivityUtils.formatDate(context, updated))
                    .replace("{1}", ActivityUtils.formatTime(context, updated)));
        }

        return sb.toString();
    }
}
