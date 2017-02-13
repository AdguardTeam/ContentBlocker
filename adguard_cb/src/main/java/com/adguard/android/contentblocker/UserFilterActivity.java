package com.adguard.android.contentblocker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.adguard.android.ServiceLocator;
import com.adguard.android.service.FilterService;
import com.adguard.android.service.PreferencesService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UserFilterActivity extends AppCompatActivity implements View.OnClickListener {

    private UserFilterAdapter userFilterAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_filter);

        setupActionBar();

        ListView userFilterList = (ListView) findViewById(R.id.user_filter_list);

        userFilterAdapter = new UserFilterAdapter(this);

        userFilterList.setAdapter(userFilterAdapter);

        findViewById(R.id.user_filter_add).setOnClickListener(this);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        View dialogView = getLayoutInflater().inflate(R.layout.new_item_dialog, null);
        final EditText newItemView = (EditText) dialogView.findViewById(R.id.newItemTextView);

        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialog)
                .setTitle(R.string.enter_rule_text)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String rule = newItemView.getText().toString();

                        if (StringUtils.isNotBlank(rule)) {
                            userFilterAdapter.addRule(rule);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();

        alertDialog.show();
    }

    private class UserFilterAdapter extends BaseAdapter {

        private List<String> ruleList = new ArrayList<>();

        private PreferencesService preferencesService;
        private FilterService filterService;
        private Activity activity;

        public UserFilterAdapter(Activity activity) {
            this.preferencesService = ServiceLocator.getInstance(activity).getPreferencesService();
            this.filterService = ServiceLocator.getInstance(activity).getFilterService();
            this.activity = activity;

            this.ruleList.addAll(preferencesService.getUserRules());
        }

        public void addRule(String rule) {
            preferencesService.addUserRuleItem(rule);

            new ApplyAndRefreshTask(filterService, activity).execute();

            refreshAdapter();
        }

        public void removeRule(String rule) {
            preferencesService.removeUserRuleItem(rule);

            new ApplyAndRefreshTask(filterService, activity).execute();

            refreshAdapter();
        }

        @Override
        public int getCount() {
            return ruleList.size();
        }

        @Override
        public Object getItem(int position) {
            return ruleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String rule = ruleList.get(position);

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.user_filter_list_item, parent, false);
            }

            TextView ruleView = (TextView) convertView.findViewById(R.id.rule_view);
            ruleView.setText(rule);

            ImageView deleteView = (ImageView) convertView.findViewById(R.id.delete_view);

            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeRule(rule);
                }
            });

            return convertView;
        }

        private void refreshAdapter() {
            ruleList.clear();
            ruleList.addAll(preferencesService.getUserRules());

            notifyDataSetChanged();
            notifyDataSetInvalidated();
        }
    }
}
