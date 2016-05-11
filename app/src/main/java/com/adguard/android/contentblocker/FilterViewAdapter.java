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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.adguard.android.model.FilterList;
import com.adguard.android.service.FilterService;

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
            view = inflater.inflate(R.layout.preference_item_checkbox, parent, false);
        } else {
            view = convertView;
        }

        ((TextView) view.findViewById(R.id.title)).setText(filterList.getName());
        ((TextView) view.findViewById(R.id.summary)).setText(filterList.getDescription());
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
        FilterList list = (FilterList) v.getTag();
        filterService.updateFilterEnabled(list, !list.isEnabled());
        ((CheckBox)v.findViewById(R.id.checkbox)).setChecked(list.isEnabled());
        new ApplyAndRefreshTask(filterService, context).execute();
    }

    public static class ApplyAndRefreshTask extends AsyncTask<Void, Void, Void> {

        private final FilterService service;
        private final Activity activity;
        private ProgressDialog dialog;

        public ApplyAndRefreshTask(FilterService service, Activity activity) {
            this.service = service;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(activity, null, activity.getText(R.string.please_wait), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            service.applyNewSettings();
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            dialog.dismiss();
        }
    }
}
