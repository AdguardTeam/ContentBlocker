package com.adguard.android.contentblocker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.adguard.android.ServiceLocator;

public class FiltersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new FilterViewAdapter(this, ServiceLocator.getInstance(getApplicationContext()).getFilterService()));
    }
}
