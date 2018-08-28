package com.adguard.android.contentblocker.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adguard.android.contentblocker.R;

public class NotificationServiceImpl implements NotificationService {
    private final Context context;
    private final Handler handler;

    public NotificationServiceImpl(Context context) {
        this.context = context;
        this.handler = new Handler();
    }

    @Override
    public void showToast(int textResId) {
        showToast(context.getString(textResId), Toast.LENGTH_LONG);
    }

    @Override
    public void showToast(final String message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    @Override
    public void showToast(final String message, final int duration) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast toast = getToast(message, duration);
            toast.show();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = getToast(message, duration);
                    toast.show();
                }
            });
        }
    }

    private Toast getToast(String message, int duration) {
        View v = LayoutInflater.from(context).inflate(R.layout.transient_notification, null);
        TextView tv = v.findViewById(android.R.id.message);
        tv.setTextSize(16);
        tv.setText(message);

        final Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(v);

        return toast;
    }
}
