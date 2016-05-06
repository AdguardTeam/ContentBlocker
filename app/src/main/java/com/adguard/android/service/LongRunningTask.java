package com.adguard.android.service;

import android.app.ProgressDialog;

import com.adguard.android.contentblocker.R;
import com.adguard.android.ui.utils.ProgressDialogUtils;
import com.adguard.commons.concurrent.DispatcherTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents any long running task
 */
public abstract class LongRunningTask implements DispatcherTask {

    protected final static Logger LOG = LoggerFactory.getLogger(LongRunningTask.class);
    protected final ProgressDialog progressDialog;

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
    protected LongRunningTask(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    /**
     * Does the task itself
     */
    protected abstract void processTask() throws Exception;

    @Override
    public void execute() throws Exception {
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
    protected void dismissProgressDialog(final ProgressDialog progressDialog) {
		ProgressDialogUtils.dismissProgressDialog(progressDialog);
	}

    /**
     * Dismisses progress dialog and shows generic error message
     *
     * @param progressDialog Progress dialog
     */
    protected void dismissProgressDialogOnError(ProgressDialog progressDialog, Exception ex) {
        LOG.warn("Dismissing progress dialog on error:\r\n", ex);

		showToastMessage(R.string.progressGenericErrorText);
		ProgressDialogUtils.dismissProgressDialog(progressDialog);
    }

    /**
     * Shows toast message
     *
     * @param resourceId Resource ID
     */
    protected void showToastMessage(int resourceId) {
        if (progressDialog != null) {
            BaseUiService.showToast(progressDialog.getOwnerActivity(), resourceId);
        }
    }

}
