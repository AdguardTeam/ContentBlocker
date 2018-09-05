/**
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
package com.adguard.android.contentblocker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adguard.android.contentblocker.commons.RawResources;
import com.adguard.android.contentblocker.model.FilterList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Filter list dao implementation (using db)
 */
public class FilterListDaoImpl implements FilterListDao {
    private static final String FILTER_LISTS_TABLE = "filter_lists";
    private static final String FILTER_LIST_ID = "filter_list_id";
    private static final String FILTER_LIST_NAME = "filter_name";
    private static final String FILTER_LIST_DESCRIPTION = "filter_description";
    private static final String FILTER_LIST_ENABLED = "enabled";
    private static final String FILTER_LIST_VERSION = "version";
    private static final String FILTER_LIST_TIME_UPDATED = "time_updated";
    private static final String FILTER_LIST_TIME_LAST_DOWNLOADED = "time_last_downloaded";
    private static final String FILTER_LIST_DISPLAY_ORDER = "display_order";

    private static final String[] COLUMNS = {
            FILTER_LIST_ID,
            FILTER_LIST_NAME,
            FILTER_LIST_DESCRIPTION,
            FILTER_LIST_ENABLED,
            FILTER_LIST_VERSION,
            FILTER_LIST_TIME_UPDATED,
            FILTER_LIST_TIME_LAST_DOWNLOADED,
            FILTER_LIST_DISPLAY_ORDER
    };

    private final Context context;
    private final DbHelper dbHelper;

    private int cachedFilterCount = 0;
    private int cachedEnabledFilterCount = 0;

    public FilterListDaoImpl(Context context, DbHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override
    public List<FilterList> selectFilterLists() {
        List<FilterList> items = new ArrayList<>();

        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            cursor = db.rawQuery(RawResources.getSelectFiltersScript(context), null);
            while (cursor.moveToNext()) {
                items.add(parseFilterList(cursor));
            }
        } finally {
            closeCursor(cursor);
        }

        return items;
    }

    @Override
    public FilterList selectFilterList(final int filterListId) {
        FilterList result = null;

        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.query(FILTER_LISTS_TABLE,
                    COLUMNS,
                    FILTER_LIST_ID + "=?",
                    new String[]{String.valueOf(filterListId)},
                    null,
                    null,
                    null);

            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0)
                    result = parseFilterList(cursor);
            }

        } finally {
            closeCursor(cursor);
        }

        return result;
    }

    @Override
    public int getFilterListCount() {
        if (cachedFilterCount > 0) {
            return cachedFilterCount;
        } else {
            cachedFilterCount = selectFilterLists().size();
            return cachedFilterCount;
        }
    }

    @Override
    public int getEnabledFilterListCount() {
        if (cachedEnabledFilterCount > 0) {
            return cachedEnabledFilterCount;
        } else {
            cachedEnabledFilterCount = 0;
            List<FilterList> filterLists = selectFilterLists();
            for (FilterList filterList : filterLists) {
                if (filterList.isEnabled()) {
                    cachedEnabledFilterCount++;
                }
            }
            return cachedEnabledFilterCount;
        }
    }

    @Override
    public void updateFilterEnabled(FilterList filter, boolean enabled) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FILTER_LIST_ENABLED, enabled ? 1 : 0);
        try {
            db.beginTransaction();
            db.update(FILTER_LISTS_TABLE, values, FILTER_LIST_ID + "=?", new String[]{Integer.toString(filter.getFilterId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        cachedEnabledFilterCount = 0;
    }

    @Override
    public void updateFilter(FilterList filter) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FILTER_LIST_VERSION, filter.getVersion().getLongVersionString());
        values.put(FILTER_LIST_TIME_UPDATED, filter.getTimeUpdated().getTime());
        values.put(FILTER_LIST_TIME_LAST_DOWNLOADED, filter.getLastTimeDownloaded().getTime());

        try {
            db.beginTransaction();
            db.update(FILTER_LISTS_TABLE, values, FILTER_LIST_ID + "=?", new String[]{Integer.toString(filter.getFilterId())});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private FilterList parseFilterList(Cursor cursor) {
        FilterList filterList = new FilterList();

        filterList.setFilterId(cursor.getInt(0));
        filterList.setName(cursor.getString(1));
        filterList.setDescription(cursor.getString(2));
        filterList.setEnabled(cursor.getInt(3) == 1);
        filterList.setVersion(cursor.getString(4));
        filterList.setTimeUpdated(new Date(cursor.getLong(5)));
        filterList.setLastTimeDownloaded(new Date(cursor.getLong(6)));
        filterList.setDisplayOrder(cursor.getInt(7));

        return filterList;
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
