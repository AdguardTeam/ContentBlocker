package com.adguard.android.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.adguard.android.contentblocker.R;
import com.adguard.android.ui.utils.ProgressDialogUtils;

/**
 * Base service class.
 */
public abstract class BaseUiService {

    /**
     * Shows progress dialog
     *
     * @param activity          Activity
     * @param titleResourceId   Title resource
     * @param messageResourceId Message resource
     * @return Progress dialog
     */
    protected static ProgressDialog showProgressDialog(Activity activity, int titleResourceId, int messageResourceId) {
		return ProgressDialogUtils.showProgressDialog(activity, titleResourceId, messageResourceId);
	}

    /**
     * Shows toast message
     *
     * @param activity Activity
     * @param message  Message
     */
    public static void showToast(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(activity);
                LayoutInflater inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflate.inflate(R.layout.transient_notification, null);
                TextView tv = (TextView)v.findViewById(android.R.id.message);
                tv.setTextSize(16);
                tv.setText(message);
                toast.setView(v);
                toast.show();
            }
        });
    }

    public static void showToast(final Activity activity, final int messageId) {
        showToast(activity, activity.getString(messageId));
    }
}
