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

        final View rateAppButton = findViewById(R.id.rateAppButton);
        rateAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebSite(AboutActivity.this, getApplicationMarketLink(getApplicationContext()));
            }
        });

        final View issuesButton = findViewById(R.id.issuesButton);
        issuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebSite(AboutActivity.this, "https://github.com/AdguardTeam/ContentBlocker/issues");
            }
        });

        final View githubLink = findViewById(R.id.githubLinkTextView);
        githubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToWebSite(AboutActivity.this, "https://github.com/AdguardTeam/ContentBlocker");
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

    /**
     * Opens web browser at specified url
     *
     * @param from Context
     * @param url  Url to open
     */
    public static void redirectToWebSite(Context from, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        from.startActivity(i);
    }
}
