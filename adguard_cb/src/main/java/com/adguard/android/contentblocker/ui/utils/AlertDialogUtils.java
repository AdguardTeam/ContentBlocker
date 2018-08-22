package com.adguard.android.contentblocker.ui.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.adguard.android.contentblocker.R;

/**
 * Helper class for showing simple alert dialogs
 */
public class AlertDialogUtils {

    /**
     * Asks for user confirmation
     *
     * @param context              Application context
     * @param title                Dialog title
     * @param message              Dialog message
     * @param confirmationListener Listener for user actions
     */
    public static void confirm(Context context, int title, int message, final ConfirmationListener confirmationListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialog)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        confirmationListener.ok();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        confirmationListener.cancel();
                    }
                })
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false);
        builder.show();
    }

    /**
     * Listener for simple confirmation dialogs
     */
    @SuppressWarnings("WeakerAccess")
    public interface ConfirmationListener {

        /**
         * Called when OK button is clicked
         */
        void ok();

        /**
         * Called when user cancels
         */
        void cancel();
    }

    /**
     * Empty listener
     */
    public static abstract class DefaultConfirmationListener implements ConfirmationListener {

        @Override
        public void ok() {
            // Override in the subclass
        }

        @Override
        public void cancel() {
            // Override in the subclass
        }
    }
}
