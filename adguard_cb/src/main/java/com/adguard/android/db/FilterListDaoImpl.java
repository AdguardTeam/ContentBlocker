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
package com.adguard.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.adguard.android.commons.RawResources;
import com.adguard.android.model.FilterList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Filter list dao implementation (using db)
 */
public class FilterListDaoImpl implements FilterListDao {

    private static final String[] COLUMNS = {
            DbHelper.FILTER_LIST_ID,
            DbHelper.FILTER_LIST_NAME,
            DbHelper.FILTER_LIST_DESCRIPTION,
            DbHelper.FILTER_LIST_ENABLED,
            DbHelper.FILTER_LIST_VERSION,
            DbHelper.FILTER_LIST_TIME_UPDATED,
            DbHelper.FILTER_LIST_TIME_LAST_DOWNLOADED,
            DbHelper.FILTER_LIST_DISPLAY_ORDER
    };

    public static String[] getColumns() {
        List<String> result = new ArrayList<>();
        result.addAll(Arrays.asList(COLUMNS));
        return result.toArray(new String[result.size()]);
    }

    private final Context context;
    private int cachedFilterCount = 0;
    private int cachedEnabledFilterCount = 0;

    public FilterListDaoImpl(Context context) {
        this.context = context;
    }

    @Override
    public void insertFilterList(FilterList filterList) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.FILTER_LIST_ID, filterList.getFilterId());
        values.put(DbHelper.FILTER_LIST_NAME, filterList.getName());
        values.put(DbHelper.FILTER_LIST_DESCRIPTION, filterList.getDescription());
        values.put(DbHelper.FILTER_LIST_ENABLED, filterList.isEnabled());
        values.put(DbHelper.FILTER_LIST_VERSION, filterList.getVersion().toString());
        values.put(DbHelper.FILTER_LIST_TIME_UPDATED, filterList.getTimeUpdated().getTime());
        values.put(DbHelper.FILTER_LIST_TIME_LAST_DOWNLOADED, filterList.getLastTimeDownloaded().getTime());
        values.put(DbHelper.FILTER_LIST_DISPLAY_ORDER, filterList.getDisplayOrder());

        SQLiteOpenHelper helper = new DbHelper(context);
        SQLiteDatabase db = null;
        try {
            db = helper.getWritableDatabase();
            db.insert(DbHelper.FILTER_LISTS_TABLE, DbHelper.FILTER_LIST_TIME_UPDATED, values);
        } finally {
            close(null, db);
        }
        cachedFilterCount = 0;
    }

    @Override
    public List<FilterList> selectFilterLists() {
        List<FilterList> items = new ArrayList<>();

        SQLiteOpenHelper helper = new DbHelper(context);
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.rawQuery(RawResources.getSelectFiltersScript(context), null);
            while (cursor.moveToNext()) {
                items.add(parseFilterList(cursor));
            }
        } finally {
            close(cursor, db);
        }

        return items;
    }

    @Override
    public FilterList selectFilterList(final int filterListId) {
        FilterList result = null;

        SQLiteOpenHelper helper = new DbHelper(context);
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();
            cursor = db.query(DbHelper.FILTER_LISTS_TABLE,
                    getColumns(),
                    DbHelper.FILTER_LIST_ID + "=?",
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
            close(cursor, db);
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
        SQLiteOpenHelper helper = new DbHelper(context);
        SQLiteDatabase db = null;
        ContentValues values = new ContentValues(1);
        values.put(DbHelper.FILTER_LIST_ENABLED, enabled ? 1 : 0);
        try {
            db = helper.getWritableDatabase();
            db.update(DbHelper.FILTER_LISTS_TABLE, values, DbHelper.FILTER_LIST_ID + "=?", new String[]{Integer.toString(filter.getFilterId())});
        } finally {
            close(null, db);
        }
        cachedEnabledFilterCount = 0;
    }

    @Override
    public void updateFilter(FilterList filter) {
        SQLiteOpenHelper helper = new DbHelper(context);
        SQLiteDatabase db = null;
        ContentValues values = new ContentValues(1);
        values.put(DbHelper.FILTER_LIST_VERSION, filter.getVersion().getLongVersionString());
        values.put(DbHelper.FILTER_LIST_TIME_UPDATED, filter.getTimeUpdated().getTime());
        values.put(DbHelper.FILTER_LIST_TIME_LAST_DOWNLOADED, filter.getLastTimeDownloaded().getTime());
        try {
            db = helper.getWritableDatabase();
            db.update(DbHelper.FILTER_LISTS_TABLE, values, DbHelper.FILTER_LIST_ID + "=?", new String[]{Integer.toString(filter.getFilterId())});
        } finally {
            close(null, db);
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

    private static void close(Cursor cursor, SQLiteDatabase database) {
        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            database.close();
        }
    }

}
