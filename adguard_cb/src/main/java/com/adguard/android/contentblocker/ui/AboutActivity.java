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
package com.adguard.android.contentblocker.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.commons.AppLink;
import com.adguard.android.contentblocker.commons.PackageUtils;
import com.adguard.android.contentblocker.ui.utils.ActivityUtils;
import com.adguard.android.contentblocker.ui.utils.NavigationHelper;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        final TextView versionInfoTextView = findViewById(R.id.versionInfoTextView);
        versionInfoTextView.setText(getString(R.string.versionInfoTextViewText).replace("{0}", PackageUtils.getVersionName(this)));

        final TextView adguardComLinkTextView = findViewById(R.id.adguardComLinkTextView);
        adguardComLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.redirectToWebSite(AboutActivity.this, AppLink.Website.getHomeUrl(getApplicationContext(), "about_activity"));
            }
        });

        final TextView forumAdguardComLinkTextView = findViewById(R.id.forumAdguardComLinkTextView);
        forumAdguardComLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.redirectToWebSite(AboutActivity.this, AppLink.Website.getForumUrl(getApplicationContext(), "about_activity"));
            }
        });

        final TextView githubLinkTextView = findViewById(R.id.githubLinkTextView);
        githubLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.redirectToWebSite(AboutActivity.this, AppLink.Github.getHomeUrl(getApplicationContext(), "about_activity"));
            }
        });

        final View rateAppButton = findViewById(R.id.rateAppButton);
        rateAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startMarket(AboutActivity.this, getPackageName(), null);
            }
        });

        final View issuesButton = findViewById(R.id.issuesButton);
        issuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.redirectToWebSite(AboutActivity.this, AppLink.Github.getNewIssueUrl(getApplicationContext(), "about_activity"));
            }
        });

        final TextView eulaLinkTextView = findViewById(R.id.eulaLinkTextView);
        eulaLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.redirectToWebSite(AboutActivity.this, AppLink.Website.getEULAUrl(getApplicationContext(), "about_activity"));
            }
        });

        final TextView privacyPolicyLinkTextView = findViewById(R.id.privacyPolicyLinkTextView);
        privacyPolicyLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.redirectToWebSite(AboutActivity.this,  AppLink.Website.getPrivacyPolicyUrl(getApplicationContext(), "about_activity"));
            }
        });
    }
}
