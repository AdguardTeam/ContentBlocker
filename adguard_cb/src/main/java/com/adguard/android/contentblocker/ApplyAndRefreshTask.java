package com.adguard.android.contentblocker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.adguard.android.service.FilterService;
import com.adguard.android.ui.utils.ProgressDialogUtils;

/**
 * Created by Revertron on 09.06.2016.
 */
public class ApplyAndRefreshTask extends AsyncTask<Void, Void, Void> {

    private final FilterService service;
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
