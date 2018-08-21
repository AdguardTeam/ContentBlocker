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
package com.adguard.android.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ui.utils.ProgressDialogUtils;

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
