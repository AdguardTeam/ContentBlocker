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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.adguard.android.commons.RawResources;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class working with local database
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DbHelper.class);

    private static final int DB_VERSION = 20;
    private static final String DB_NAME = "adguard.db";

    public static final String FILTER_LISTS_TABLE = "filter_lists";
    public static final String FILTER_LIST_ID = "filter_list_id";
    public static final String FILTER_LIST_NAME = "filter_name";
    public static final String FILTER_LIST_DESCRIPTION = "filter_description";
    public static final String FILTER_LIST_ENABLED = "enabled";
    public static final String FILTER_LIST_VERSION = "version";
    public static final String FILTER_LIST_TIME_UPDATED = "time_updated";
    public static final String FILTER_LIST_TIME_LAST_DOWNLOADED = "time_last_downloaded";
    public static final String FILTER_LIST_DISPLAY_ORDER = "display_order";
    private final Context context;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LOG.info("DbHelper.onCreate()");
        try {
            db.beginTransaction();

            createTables(db);
            fillFilters(db);
            fillFiltersLocalization(db);
            enableDefaultFilters(db);

            db.setTransactionSuccessful();
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOG.info("Performing database upgrade {}=>{}.", oldVersion, newVersion);


        for (int i = 0; i < (newVersion - oldVersion); i++) {
            int prevDbVersion = oldVersion + i;
            int newDbVersion = oldVersion + i + 1;
            String updateScript = RawResources.getUpdateScript(context, prevDbVersion, newDbVersion);

            if (updateScript != null) {
                LOG.info("Found an update script {}=>{}. Applying it.", prevDbVersion, newDbVersion);
                executeSql(db, updateScript);
            } else {
                LOG.warn("Update script not found for {}=>{}, recreating DB.", prevDbVersion, newDbVersion);
                executeSql(db, RawResources.getDropTablesScript(context));
                onCreate(db);
                return;
            }
        }

        LOG.info("Performing database upgrade...success");
    }

    private void enableDefaultFilters(SQLiteDatabase db) {
        LOG.info("Enabling default filters...");

        executeSql(db, RawResources.getEnableDefaultFiltersScript(context));
    }

    private void fillFilters(SQLiteDatabase db) {
        LOG.info("Filling database filters table...");
        String script = RawResources.getInsertFiltersScript(context);

        executeSql(db, script);
    }

    private void fillFiltersLocalization(SQLiteDatabase db) {
        LOG.info("Filling database filters localization table...");
        String script = RawResources.getInsertFiltersLocalizationScript(context);

        executeSql(db, script);
    }

    private void executeSql(SQLiteDatabase db, String script) {
        for (String sql : StringUtils.split(script, ";")) {
            if (!StringUtils.isWhitespace(sql)) {
                LOG.info("Execute sql: {}", sql);
                db.execSQL(sql);
            }
        }
    }

    private void createTables(SQLiteDatabase db) {
        LOG.info("Creating database tables...");

        executeSql(db, RawResources.getCreateTablesScript(context));
    }
}
