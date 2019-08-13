package com.adguard.android.contentblocker.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.ui.utils.NavigationHelper;

import java.util.Date;

public class RateServiceImpl implements RateService {

    private static final long FIRST_FLEX_PERIOD = 24 * 60 * 60 * 1000;
    private static final long SECOND_FLEX_PERIOD = 7 * FIRST_FLEX_PERIOD;

    private PreferencesService preferencesService;
    private final Context context;

    public RateServiceImpl(Context context) {
        this.context = context;
        this.preferencesService = ServiceLocator.getInstance(context).getPreferencesService();
        checkFirstLaunch();
    }

    @Override
    public void showRateDialog(Activity activity) {
        showDialog(activity);
//        if (preferencesService.isAppRated()) {
//            return;
//        }
//
//        long scheduledTime = preferencesService.getLastTimeCommunication();
//        if (new Date().after(new Date(scheduledTime))) {
//
//        }
    }

    private void checkFirstLaunch() {
        if (preferencesService.getLastTimeCommunication() == 0) {
            // It's first launch. We should schedule next check
            preferencesService.setLastUpdateCheck(new Date().getTime() + FIRST_FLEX_PERIOD);
        }
    }

    private void showDialog(final Activity activity) {
        ServiceLocator.getInstance(context).getNotificationService().showRateAppNotification();
        final LayoutInflater inflater = LayoutInflater.from(activity);
        final View dialogLayout = inflater.inflate(R.layout.rate_dialog, null);
        final ViewGroup starsLayout = dialogLayout.findViewById(R.id.stars_layout);
        final ViewGroup feedback = dialogLayout.findViewById(R.id.feedback_layout);
        final ViewGroup buttonsLater = dialogLayout.findViewById(R.id.first_buttons);
        final ViewGroup buttonsSubmit = dialogLayout.findViewById(R.id.second_buttons);
        final EditText feedbackText = dialogLayout.findViewById(R.id.feedback);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(dialogLayout);
        final AlertDialog dialog = builder.show();

        View.OnClickListener starsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = (int) v.getTag();
                refreshDialogView(context, dialog, starsLayout, feedback, buttonsLater, buttonsSubmit, count);
            }
        };

        for (int i = 0; i < starsLayout.getChildCount(); i++) {
            starsLayout.getChildAt(i).setTag(i + 1);
            starsLayout.getChildAt(i).setOnClickListener(starsListener);
        }

        View.OnClickListener buttonsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_never:
                        ServiceLocator.getInstance(context).getNotificationService().showToast("Never");
                        break;
                    case R.id.button_later:
                        ServiceLocator.getInstance(context).getNotificationService().showToast("Later");
                        break;
                    case R.id.button_cancel:
                        ServiceLocator.getInstance(context).getNotificationService().showToast("Cancel");
                        break;
                    case R.id.button_submit:
                        ServiceLocator.getInstance(context).getNotificationService().showToast(feedbackText.getText().toString());
                        break;

                }
                dialog.cancel();
            }
        };

        dialogLayout.findViewById(R.id.button_never).setOnClickListener(buttonsListener);
        dialogLayout.findViewById(R.id.button_later).setOnClickListener(buttonsListener);
        dialogLayout.findViewById(R.id.button_cancel).setOnClickListener(buttonsListener);
        dialogLayout.findViewById(R.id.button_submit).setOnClickListener(buttonsListener);
    }

    private void refreshDialogView(Context context, AlertDialog dialog, ViewGroup stars, ViewGroup feedback, ViewGroup buttonsLater, ViewGroup buttonsSubmit, int count) {
        buttonsLater.setVisibility(View.GONE);
        for (int i = 0; i < stars.getChildCount(); i++) {
            ((ImageView) stars.getChildAt(i)).setImageDrawable(context.getDrawable(i < count ? R.drawable.ic_star_filled :
                    R.drawable.ic_star_empty));
        }

        if (count > 3) {
            feedback.setVisibility(View.GONE);
            buttonsSubmit.setVisibility(View.GONE);
            NavigationHelper.redirectToPlayMarket(context);
            dialog.cancel();
        } else {
            feedback.setVisibility(View.VISIBLE);
            buttonsSubmit.setVisibility(View.VISIBLE);
        }
    }

}
