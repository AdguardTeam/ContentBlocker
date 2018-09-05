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
package com.adguard.android.contentblocker.service;

import android.app.Activity;
import android.app.ProgressDialog;

import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.ui.utils.ProgressDialogUtils;
import com.adguard.android.contentblocker.commons.concurrent.DispatcherTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents any long running task
 */
public abstract class LongRunningTask implements DispatcherTask {

    protected final static Logger LOG = LoggerFactory.getLogger(LongRunningTask.class);
    private final ProgressDialog progressDialog;

    /**
     * Creates an instance of the LongRunningTask
     * which do not use progress dialog
     */
    protected LongRunningTask() {
        this(null);
    }

    /**
     * Creates an instance of the LongRunningTask
     *
     * @param progressDialog Progress dialog to show while task is in process
     */
    LongRunningTask(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    /**
     * Does the task itself
     */
    protected abstract void processTask() throws Exception;

    @Override
    public void execute() {
        try {
            LOG.info("Start task {} execution", this.getClass().getSimpleName());
            processTask();
        } catch (Exception ex) {
            dismissProgressDialogOnError(progressDialog, ex);
        } finally {
            dismissProgressDialog(progressDialog);
            LOG.info("Finished task {} execution", this.getClass().getSimpleName());
        }
    }

    /**
     * Dismisses progress dialog
     *
     * @param progressDialog Progress dialog to dismiss
     */
    private void dismissProgressDialog(final ProgressDialog progressDialog) {
		ProgressDialogUtils.dismissProgressDialog(progressDialog);
	}

    /**
     * Dismisses progress dialog and shows generic error message
     *
     * @param progressDialog Progress dialog
     */
    private void dismissProgressDialogOnError(ProgressDialog progressDialog, Exception ex) {
        LOG.warn("Dismissing progress dialog on error:\r\n", ex);

        Activity ownerActivity = progressDialog.getOwnerActivity();
        if (ownerActivity == null) {
            return;
        }

        NotificationService notificationService = ServiceLocator.getInstance(ownerActivity).getNotificationService();
        notificationService.showToast(R.string.progressGenericErrorText);
		ProgressDialogUtils.dismissProgressDialog(progressDialog);
    }
}
