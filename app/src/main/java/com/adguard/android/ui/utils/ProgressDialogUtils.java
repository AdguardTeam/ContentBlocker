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
