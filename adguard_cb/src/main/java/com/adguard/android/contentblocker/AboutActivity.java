/**
 This file is part of Adguard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2018 Adguard Software Ltd. All rights reserved.

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
package com.adguard.android.contentblocker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.adguard.android.ui.utils.NavigationHelper;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final TextView versionInfoTextView = (TextView) findViewById(R.id.versionInfoTextView);
        String versionName = getVersionName();
        versionInfoTextView.setText(versionInfoTextView.getText().toString().replace("{0}", versionName));

        final TextView adguardComLinkTextView = (TextView) findViewById(R.id.adguardComLinkTextView);
        adguardComLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        final TextView forumAdguardComLinkTextView = (TextView) findViewById(R.id.forumAdguardComLinkTextView);
        forumAdguardComLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        final TextView githubLinkTextView = (TextView) findViewById(R.id.githubLinkTextView);
        githubLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        final View rateAppButton = findViewById(R.id.rateAppButton);
        rateAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.redirectToWebSite(AboutActivity.this, getApplicationMarketLink(getApplicationContext()));
            }
        });

        final View issuesButton = findViewById(R.id.issuesButton);
        issuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.redirectToWebSite(AboutActivity.this,"https://github.com/AdguardTeam/ContentBlocker/issues");
            }
        });

        final TextView eulaLinkTextView = (TextView) findViewById(R.id.eulaLinkTextView);
        eulaLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        final TextView privacyPolicyLinkTextView = (TextView) findViewById(R.id.privacyPolicyLinkTextView);
        privacyPolicyLinkTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public String getVersionName() {
        PackageInfo packageInfo = getPackageInfo(this, this.getPackageName());
        if (packageInfo != null) {
            return packageInfo.versionName;
        }

        // Default version name
        return "1.0";
    }

    /**
     * Gets package info by the package name.
     * If package is not installed - returns null.
     *
     * @param context     Android context
     * @param packageName Package name
     * @return Package info or null
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        try {
            return packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * @return Link to market
     */
    public static String getApplicationMarketLink(Context context) {
        return "https://play.google.com/store/apps/details?id=" + context.getPackageName();
    }
}
