/*
 * This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 * Copyright © 2018 AdGuard Content Blocker. All rights reserved.
 * <p/>
 * AdGuard Content Blocker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * <p/>
 * AdGuard Content Blocker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.contentblocker.onboarding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.commons.BrowserUtils;
import com.adguard.android.contentblocker.service.PreferencesService;
import com.adguard.android.contentblocker.ui.ClickViewPager;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;

import static com.adguard.android.contentblocker.commons.BrowserUtils.YANDEX_BROWSER_PACKAGE;

public class OnboardingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "Onboarding";

    private ClickViewPager viewPager;

    private ImageView[] indicators;

    private int page = 0;
    private boolean browsersFound = false;

    private PackageReceiver packageReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // Remove all fragments on configuration change (screen rotation)
        removeAllFragments(supportFragmentManager);

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(this, supportFragmentManager);

        indicators = new ImageView[]{
                findViewById(R.id.intro_indicator_0),
                findViewById(R.id.intro_indicator_1),
                findViewById(R.id.intro_indicator_2)
        };

        // Set up the ViewPager with the sections adapter.
        viewPager = findViewById(R.id.container);
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(mSectionsPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                updateIndicators(page);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        Set<String> browsersAvailable = BrowserUtils.getBrowsersAvailableByIntent(getApplicationContext());
        for (String browser : browsersAvailable) {
            Log.i(TAG, "Browser found: " + browser);
            browsersFound = true;
            indicators[1].setVisibility(View.GONE);
        }
        updateIndicators(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (page == 1) {
            Set<String> browsersAvailable = BrowserUtils.getBrowsersAvailableByIntent(getApplicationContext());
            for (String browser : browsersAvailable) {
                Log.i(TAG, "Browser installed: " + browser);
                browsersFound = true;
            }
            if (browsersFound) {
                viewPager.setCurrentItem(2, false);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (packageReceiver != null) {
            getApplicationContext().unregisterReceiver(packageReceiver);
            packageReceiver = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_onboarding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (page == 0) {
            if (browsersFound) {
                viewPager.setCurrentItem(2, false);
            } else {
                viewPager.setCurrentItem(1, true);
            }
        } else if (page == 1) {
            BrowserUtils.showBrowserInstallDialog(this);
            startPackageReceiver();
        } else {
            onLastPageClick();
        }
    }

    public void onLastPageClick() {
        if (BrowserUtils.isYandexBrowserAvailable(getApplicationContext())) {
            if (browsersFound) {
                BrowserUtils.openYandexBlockingOptions(getApplicationContext());
            } else {
                BrowserUtils.startYandexBrowser(getApplicationContext());
            }

        } else {
            BrowserUtils.openSamsungBlockingOptions(getApplicationContext());
        }

        PreferencesService preferencesService = ServiceLocator.getInstance(getApplicationContext()).getPreferencesService();
        preferencesService.setOnboardingShown(true);

        finish();
    }

    private void startPackageReceiver() {
        packageReceiver = new PackageReceiver(OnboardingActivity.this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addDataScheme("package");
        getApplicationContext().registerReceiver(packageReceiver, filter);
        Log.i(TAG, "Registered package receiver...");
    }

    @Override
    public void onBackPressed() {
        if (page > 0) {
            if (browsersFound) {
                viewPager.setCurrentItem(0, false);
            } else {
                viewPager.setCurrentItem(page - 1, true);
            }
        }
    }

    private void removeAllFragments(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        List<Fragment> fragments = fragmentManager.getFragments();
        if (CollectionUtils.isNotEmpty(fragments)) {
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    Log.i(TAG, "Removing fragment " + fragment.toString());
                    transaction.remove(fragment);
                }
            }
        }
        transaction.commit();
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    private void onNewBrowserInstalled() {
        Log.i(TAG, "onNewBrowserInstalled()");
        if (page < 2) {
            viewPager.setCurrentItem(2, false);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private View.OnClickListener listener;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(View.OnClickListener listener, int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            fragment.listener = listener;
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int page = 0;

            Bundle arguments = getArguments();
            if (arguments != null) {
                page = arguments.getInt(ARG_SECTION_NUMBER);
            }

            View rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);

            ImageView image = rootView.findViewById(R.id.sectionImage);
            image.setImageResource(getImageForBrowser(page));

            TextView titleView = rootView.findViewById(R.id.sectionTitle);
            titleView.setText(getTitleForPage(page));

            TextView textView = rootView.findViewById(R.id.sectionMessage);
            textView.setText(getTextForPage(page));

            AppCompatButton button = rootView.findViewById(R.id.sectionButton);
            button.setText(getButtonTextForPage(page));
            button.setOnClickListener(listener);

            return rootView;
        }

        private String getTitleForPage(int page) {
            switch (page) {
                case 1:
                    return getString(R.string.onboarding_title1);
                case 2:
                    return getString(R.string.onboarding_title2);
                case 3:
                    return getString(R.string.onboarding_title3);
                default:
                    return null;
            }
        }

        private String getTextForPage(int page) {
            switch (page) {
                case 1:
                    return getString(R.string.onboarding_text1);
                case 2:
                    return getString(R.string.onboarding_text2);
                case 3:
                    return getString(R.string.onboarding_text3);
                default:
                    return null;
            }
        }

        private String getButtonTextForPage(int page) {
            switch (page) {
                case 1:
                    return getString(R.string.onboarding_button_text1);
                case 2:
                    return getString(R.string.onboarding_button_text2);
                case 3:
                    return getString(R.string.onboarding_button_text3);
                default:
                    return null;
            }
        }

        private int getImageForBrowser(int page) {
            switch (page) {
                case 1:
                    return R.drawable.onboarding_1;
                case 2:
                    return R.drawable.onboarding_2;
                case 3:
                    return R.drawable.onboarding_3;
                default:
                    return 0;
            }
        }
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final View.OnClickListener listener;

        SectionsPagerAdapter(View.OnClickListener listener, FragmentManager fm) {
            super(fm);
            this.listener = listener;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(listener, position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    public static class PackageReceiver extends BroadcastReceiver {
        private final OnboardingActivity activity;

        public PackageReceiver(OnboardingActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: " + intent.toString());
            if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction()) && intent.getData() != null) {
                String intentPackageName = intent.getData().getSchemeSpecificPart();

                Log.i(TAG, "package = " + intentPackageName);
                if (intentPackageName.startsWith(YANDEX_BROWSER_PACKAGE)) {
                    activity.onNewBrowserInstalled();
                }
            }
        }
    }
}
