package com.adguard.android.contentblocker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.adguard.android.ui.utils.ActivityUtils;

public class NoBrowsersFoundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_browsers_found);

        findViewById(R.id.install_samsung_browser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startMarket(NoBrowsersFoundActivity.this, "com.sec.android.app.sbrowser");
                finish();
            }
        });

        findViewById(R.id.install_yandex_browser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startMarket(NoBrowsersFoundActivity.this, "com.yandex.browser.alpha");
                finish();
            }
        });
    }
}
