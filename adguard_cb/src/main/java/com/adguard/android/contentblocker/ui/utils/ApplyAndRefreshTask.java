/*
 * This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 * Copyright © 2018 AdGuard Content Blocker. All rights reserved.
 * <p/>
 * AdGuard Content Blocker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * <p/>
 * AdGuard Content Blocker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.contentblocker.ui.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.adguard.android.contentblocker.R;
import com.adguard.android.service.FilterService;
import com.adguard.android.contentblocker.ui.utils.ProgressDialogUtils;

public class ApplyAndRefreshTask extends AsyncTask<Void, Void, Void> {

    private final FilterService service;
    @SuppressLint("StaticFieldLeak")
    private final Activity activity;
    private ProgressDialog dialog;

    public ApplyAndRefreshTask(FilterService service, Activity activity) {
        this.service = service;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialogUtils.showProgressDialog(activity, -1, R.string.please_wait);
    }

    @Override
    protected Void doInBackground(Void... params) {
        service.applyNewSettings();
        return null;
    }

    @Override
    protected void onPostExecute(Void res) {
        ProgressDialogUtils.dismissProgressDialog(dialog);
    }
}
