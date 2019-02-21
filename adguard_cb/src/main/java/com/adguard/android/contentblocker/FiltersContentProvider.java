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
package com.adguard.android.contentblocker;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;

public class FiltersContentProvider extends ContentProvider {

    private static final String FILTERS_FILE_PATH = "/filters.txt";

    private String filtersPath;

    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        File filterFile = new File(filtersPath);
        if (!filterFile.exists()) {
            ServiceLocator.getInstance(getContext()).getFilterService().applyNewSettings();
        }

        return ParcelFileDescriptor.open(filterFile, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return "text/plain";
    }

    @Override
    public boolean onCreate() {
        filtersPath = getContext().getFilesDir().getAbsolutePath() + FILTERS_FILE_PATH;
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
