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
package com.adguard.android.contentblocker.preferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.adguard.android.contentblocker.R;

/**
 *
 */
public class PreferenceViewAdapter extends BaseAdapter {

    private final PreferenceDb prefs;
    private final View.OnClickListener listener;
    private final Context context;
    private final LayoutInflater layoutInflater;

    private final View.OnClickListener booleanPrefListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBooleanClicked(v);
        }
    };

    public PreferenceViewAdapter(Context context, PreferenceDb prefs, View.OnClickListener listener) {
        this.context = context;
        this.prefs = prefs;
        this.listener = listener;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return prefs.size();
    }

    @Override
    public Object getItem(int position) {
        return prefs.getPreference(position);
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
        PreferenceItem item = prefs.getPreference(position);
        View view;
        if (convertView == null) {
            view = inflater.inflate(prefs.getPreferenceLayoutId(item.name), parent, false);
        } else {
            view = convertView;
        }

        ((TextView)view.findViewById(R.id.title)).setText(item.title);
        ((TextView)view.findViewById(R.id.summary)).setText(item.summary);
        if (item.value instanceof Boolean) {
            ((CheckBox)view.findViewById(R.id.checkbox)).setChecked(((Boolean) item.value).booleanValue());
            view.setOnClickListener(booleanPrefListener);
        } else if (item.value == null) {
            // Let the activity handle this click
            view.setOnClickListener(listener);
        }

        view.setTag(item.name);

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return prefs.getPreferenceTypeId(position);
    }

    @Override
    public int getViewTypeCount() {
        return prefs.getPreferenceTypeCount();
    }

    private void onBooleanClicked(View view) {
        String name = (String) view.getTag();
        PreferenceItem item = prefs.getPreference(name);
        prefs.setPreference(name, !((Boolean)item.value));
        ((CheckBox)view.findViewById(R.id.checkbox)).setChecked(((Boolean) item.value).booleanValue());
    }
}
