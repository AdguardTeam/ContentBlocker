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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.adguard.android.contentblocker.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class PreferenceDb {
    public static final String PREF_AUTO_UPDATE = "pref_auto_update";
    public static final String PREF_UPDATE_WIFI_ONLY = "pref_update_wifi_only";
    public static final String PREF_SHOW_USEFUL_ADS = "pref_show_useful_ads";
    public static final String PREF_FILTERS = "pref_filters";
    private HashMap<String, PreferenceItem> itemsMap = new HashMap<>();
    private List<PreferenceItem> itemsList = new ArrayList<>();
    private Context context;

    public PreferenceDb(Context context) {
        this.context = context;

        // Adding items to our pref-base
        addPreference(PREF_AUTO_UPDATE, R.string.pref_autoupdate_filters, R.string.pref_summary_autoupdate_filters, true);
        addPreference(PREF_UPDATE_WIFI_ONLY, R.string.pref_update_wifi_only, R.string.pref_summary_update_wifi_only, false);
        addPreference(PREF_SHOW_USEFUL_ADS, R.string.pref_show_useful_ads, R.string.pref_summary_show_useful_ads, true);
        addPreference(PREF_FILTERS, R.string.pref_filters_category, R.string.pref_summary_filters_category, null);
        // And refreshing their values from preferences
        refreshItems();
    }

    public void addPreference(String name, String title, String summary, Object value) {
        PreferenceItem item = new PreferenceItem(name, title, summary, value);
        itemsList.add(item);
        itemsMap.put(name, item);
    }

    public void addPreference(String name, int titleResId, int summaryResId, Object value) {
        addPreference(name, context.getString(titleResId), context.getString(summaryResId), value);
    }

    public void setPreference(String name, Object value) {
        PreferenceItem item = itemsMap.get(name);
        if (item == null) {
            throw new IllegalArgumentException("Error setting preference " + name + "! No such preference!");
        }

        item.value = value;
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        if (item.value instanceof Boolean) {
            editor.putBoolean(name, (Boolean) value);
        } else if (item.value instanceof Integer) {
            editor.putInt(name, (Integer) item.value);
        }
        // TODO other variants
        editor.commit();
    }

    private void refreshItems() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        for (PreferenceItem item : itemsList) {
            if (item.value instanceof Boolean) {
                item.value = prefs.getBoolean(item.name, (Boolean) item.value);
            } else if (item.value instanceof Integer) {
                item.value = prefs.getInt(item.name, (Integer) item.value);
            } else if (item.value instanceof String) {
                item.value = prefs.getString(item.name, (String) item.value);
            } else if (item.value instanceof String[]) {
                item.value = StringUtils.split(prefs.getString(item.name, (String) item.value), System.lineSeparator());
            }
        }
    }

    public PreferenceItem getPreference(int index) {
        return itemsList.get(index);
    }

    public PreferenceItem getPreference(String name) {
        return itemsMap.get(name);
    }

    public int size() {
        return itemsList.size();
    }

    public int getPreferenceLayoutId(String name) {
        int type = getPreferenceTypeId(name);

        switch (type) {
            case 0:
                return R.layout.preference_item_checkbox;
            case 1:
                return R.layout.preference_item_number;
            case 2:
                return R.layout.preference_item_text;
            case 3:
                return R.layout.preference_item_stringlist;
            default:
                return R.layout.preference_item_subgroup;
        }
    }

    public int getPreferenceTypeId(String name) {
        PreferenceItem item = itemsMap.get(name);
        if (item == null) {
            throw new IllegalArgumentException("Error getting preference " + name + "! No such preference!");
        }

        return getPreferenceTypeId(item);
    }

    public int getPreferenceTypeId(int index) {
        PreferenceItem item = itemsList.get(index);
        if (item == null) {
            throw new IllegalArgumentException("Error getting preference " + index + "! No such preference!");
        }

        return getPreferenceTypeId(item);
    }

    public int getPreferenceTypeCount() {
        return 5;
    }

    private static int getPreferenceTypeId(PreferenceItem item) {
        if (item.value == null) {
            return -1;
        } else if (item.value instanceof Boolean) {
            return 0;
        } else if (item.value instanceof Integer) {
            return 1;
        } else if (item.value instanceof String) {
            return 2;
        } else if (item.value instanceof String[]) {
            return 3;
        }
        return -1;
    }
}
