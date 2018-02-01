/**
 This file is part of Adguard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2018 Adguard Software Ltd. All rights reserved.

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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.adguard.android.ServiceLocator;
import com.adguard.android.service.PreferencesService;

import java.io.File;
import java.io.FileNotFoundException;

public class FiltersContentProvider extends ContentProvider {

    public static final String ACTION_CONNECTED = "com.adguard.contentblocker.ACTION_CONNECTED";
    private String filtersPath;

    public FiltersContentProvider() {
    }

    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        ParcelFileDescriptor parcel = ParcelFileDescriptor.open(new File(filtersPath), ParcelFileDescriptor.MODE_READ_ONLY);
        Log.i("FiltersContentProvider", "Browser opened filters...");
        PreferencesService preferencesService = ServiceLocator.getInstance(getContext().getApplicationContext()).getPreferencesService();
        preferencesService.incBrowserConnectedCount();
        return parcel;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return "text/plain";
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean onCreate() {
        filtersPath = getContext().getFilesDir().getAbsolutePath() + "/filters.txt";
        File f = new File(filtersPath);
        if (!f.exists()) {
            ServiceLocator.getInstance(getContext().getApplicationContext()).getFilterService().applyNewSettings();
        }
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
