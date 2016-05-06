package com.adguard.android.contentblocker;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.adguard.android.ServiceLocator;

import java.io.File;
import java.io.FileNotFoundException;

public class FiltersContentProvider extends ContentProvider {

    private String filtersPath;

    public FiltersContentProvider() {
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        ParcelFileDescriptor parcel = ParcelFileDescriptor.open(new File(filtersPath), ParcelFileDescriptor.MODE_READ_ONLY);
        return parcel;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getType(Uri uri) {
        return "text/plain";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean onCreate() {
        filtersPath = getContext().getFilesDir().getAbsolutePath() + "/filters.txt";
        File f = new File(filtersPath);
        if (!f.exists()) {
            ServiceLocator.getInstance(getContext()).getFilterService().scheduleFiltersUpdate();
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
