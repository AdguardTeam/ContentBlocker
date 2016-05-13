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
package com.adguard.android.ui.utils;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Helper class for progress dialogs.
 */
public class ProgressDialogUtils {

	/**
	 * Shows progress dialog and locks activity orientation change.
	 *
	 * @param activity requester activity
	 * @param titleResourceId progress dialog title resource id
	 * @param messageResourceId progress dialog message resource id
	 * @return progress dialog
	 */
	public static ProgressDialog showProgressDialog(Activity activity, int titleResourceId, int messageResourceId) {
		if (activity == null) {
			return null;
		}

		ActivityUtils.lockOrientation(activity);

		ProgressDialog progressDialog = ProgressDialog.show(
				activity,
				titleResourceId > 0? activity.getString(titleResourceId) : null,
				activity.getString(messageResourceId),
				false,
				false);
		progressDialog.setOwnerActivity(activity);

		return progressDialog;
	}

	/**
	 * Dismiss provided progress dialog and then unlocks activity orientation change.
	 *
	 * @param progressDialog progress dialog
	 */
	public static void dismissProgressDialog(final ProgressDialog progressDialog) {
		//http://jira.performix.ru/browse/AG-6026
		try {
			if (progressDialog != null && progressDialog.isShowing()
					&& !progressDialog.getOwnerActivity().isChangingConfigurations()
					&& !progressDialog.getOwnerActivity().isFinishing()) {
				progressDialog.dismiss();

				ActivityUtils.unlockOrientation(progressDialog.getOwnerActivity());
			}
		} catch (Exception e) {
			//Ignore
		}
	}
}
