package com.adguard.android.contentblocker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.adguard.android.contentblocker.preferences.*;
import com.adguard.android.contentblocker.preferences.PreferenceViewAdapter;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
        setContentView(R.layout.activity_settings);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new PreferenceViewAdapter(this, new PreferenceDb(getApplicationContext()), this));
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof String) {
            String tagString = (String) tag;
            if (tagString.equals(PreferenceDb.PREF_FILTERS)) {
                startActivity(new Intent(this, FiltersActivity.class));
            }
        }
    }
}
