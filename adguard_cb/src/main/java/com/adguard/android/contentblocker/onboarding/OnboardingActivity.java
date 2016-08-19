package com.adguard.android.contentblocker.onboarding;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.adguard.android.ServiceLocator;
import com.adguard.android.commons.BrowserUtils;
import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ui.ClickViewPager;
import com.adguard.android.service.PreferencesService;

import java.util.Set;

public class OnboardingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "Onboarding";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ClickViewPager viewPager;

    private ImageView[] indicators;
    private int page = 0;
    private boolean browsersFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        indicators = new ImageView[]{
                (ImageView) findViewById(R.id.intro_indicator_0),
                (ImageView) findViewById(R.id.intro_indicator_1),
                (ImageView) findViewById(R.id.intro_indicator_2)
        };

        // Set up the ViewPager with the sections adapter.
        viewPager = (ClickViewPager) findViewById(R.id.container);
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
        Set<String> browsersAvailable = BrowserUtils.getBrowsersAvailable(getApplicationContext());
        for (String browser : browsersAvailable) {
            Log.i(TAG, "Browser found: " + browser);
            browsersFound = true;
            indicators[1].setVisibility(View.GONE);
        }
        updateIndicators(0);
    }

    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (page == 1) {
            Set<String> browsersAvailable = BrowserUtils.getBrowsersAvailable(getApplicationContext());
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
        } else {
            Set<String> browsersAvailable = BrowserUtils.getBrowsersAvailable(getApplicationContext());
            if (browsersAvailable.contains(BrowserUtils.YANDEX)) {
                BrowserUtils.openYandexBlockingOptions(getApplicationContext());
            } else if (browsersAvailable.contains(BrowserUtils.SAMSUNG)) {
                BrowserUtils.openSamsungBlockingOptions(getApplicationContext());
            }
            PreferencesService preferencesService = ServiceLocator.getInstance(getApplicationContext()).getPreferencesService();
            preferencesService.setOnboardingShown(true);
            finish();
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int page = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);

            ImageView image = (ImageView) rootView.findViewById(R.id.sectionImage);
            image.setImageResource(getImageForBrowser(page, 1));

            TextView titleView = (TextView) rootView.findViewById(R.id.sectionTitle);
            titleView.setText(getTitleForPage(page));

            TextView textView = (TextView) rootView.findViewById(R.id.sectionMessage);
            textView.setText(getTextForPage(page));

            AppCompatButton button = (AppCompatButton) rootView.findViewById(R.id.sectionButton);
            button.setText(getButtonTextForPage(page));
            button.setOnClickListener(listener);

            return rootView;
        }

        private String getTitleForPage(int page) {
            switch (page) {
                case 1: return getString(R.string.onboarding_title1);
                case 2: return getString(R.string.onboarding_title2);
                case 3: return getString(R.string.onboarding_title3);
                default: return null;
            }
        }

        private String getTextForPage(int page) {
            switch (page) {
                case 1: return getString(R.string.onboarding_text1);
                case 2: return getString(R.string.onboarding_text2);
                case 3: return getString(R.string.onboarding_text3);
                default: return null;
            }
        }

        private String getButtonTextForPage(int page) {
            switch (page) {
                case 1: return getString(R.string.onboarding_button_text1);
                case 2: return getString(R.string.onboarding_button_text2);
                case 3: return getString(R.string.onboarding_button_text3);
                default: return null;
            }
        }

        private int getImageForBrowser(int page, int browser) {
            switch (page) {
                case 1: return R.drawable.onboarding_image1;
                case 2: return R.drawable.onboarding_image2;
                case 3:
                    switch (browser) {
                        case 1: return R.drawable.onboarding_browser_yandex;
                        case 2: return R.drawable.onboarding_browser_samsung;
                        default: return 0;
                    }
                default: return 0;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final View.OnClickListener listener;

        public SectionsPagerAdapter(View.OnClickListener listener, FragmentManager fm) {
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
}
