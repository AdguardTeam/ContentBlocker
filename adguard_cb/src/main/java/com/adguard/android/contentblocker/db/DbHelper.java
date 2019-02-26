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
package com.adguard.android.contentblocker.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.adguard.android.contentblocker.commons.RawResources;
import com.adguard.android.contentblocker.service.PreferencesService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class working with local database
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final Logger LOG = LoggerFactory.getLogger(DbHelper.class);

    private static final int DB_VERSION = 22;
    private static final String DB_NAME = "adguard.db";


    private final Context context;
    private final PreferenceUpgrade preferenceUpgrade;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        this.preferenceUpgrade = new PreferenceUpgrade(context);
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
                LOG.info("Update script not found for {}=>{}, recreating DB.", prevDbVersion, newDbVersion);
            }
        }

        // Refresh filters and localizations
        fillFilters(db);
        fillFiltersLocalization(db);

        preferenceUpgrade.onUpgrade(oldVersion, newVersion);

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

    private static class PreferenceUpgrade {
        private final Context context;

        PreferenceUpgrade(Context context) {
            this.context = context;
        }

        void onUpgrade(int oldVersion, int newVersion) {
            if (oldVersion < newVersion) {
                for (int version = oldVersion + 1; version <= newVersion; version++) {
                    upgradeUserFilter(version);
                }
            }
        }

        private void upgradeUserFilter(int version) {
            if (version == 21) {
                LOG.info("v2.2 upgrade: user filter conversion");

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                String oldUserRulesKey = "user_rules";
                if (sharedPreferences.contains(oldUserRulesKey)) {
                    Set<String> oldUserRules = sharedPreferences.getStringSet(oldUserRulesKey, new HashSet<String>());
                    if (!oldUserRules.isEmpty()) {
                        String rules = StringUtils.join(oldUserRules, "\n");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(oldUserRulesKey);
                        editor.putString(PreferencesService.KEY_USER_RULES_STRING, rules);
                        editor.apply();

                        LOG.info("{} user rules converted", oldUserRules.size());
                    }
                }

            }
        }
    }
}
