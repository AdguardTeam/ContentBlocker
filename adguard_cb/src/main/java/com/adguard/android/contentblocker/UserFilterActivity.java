package com.adguard.android.contentblocker;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.adguard.android.ServiceLocator;
import com.adguard.android.service.FilterService;
import com.adguard.android.service.FilterServiceImpl;
import com.adguard.android.service.PreferencesService;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserFilterActivity extends AppCompatActivity implements FilterServiceImpl.OnImportListener {

    private static final int REQUEST_CODE = 1237;

    private UserFilterAdapter userFilterAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_filter);

        setupActionBar();

        View emptyListWrapper = findViewById(R.id.userfilterEmptyWrapper);
        ListView userFilterList = (ListView) findViewById(R.id.user_filter_list);

        userFilterAdapter = new UserFilterAdapter(this);

        userFilterList.setEmptyView(emptyListWrapper);
        userFilterList.setAdapter(userFilterAdapter);

        findViewById(R.id.user_filter_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewItemDialog();
            }
        });

        findViewById(R.id.user_filter_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_filter_add:
                showNewItemDialog();
                break;
            case R.id.user_filter_import:
                showImportDialog();
                break;
            case R.id.user_filter_clear:
                userFilterAdapter.clearRules();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                ServiceLocator.getInstance(this).getFilterService().importUserRulesFromUrl(this, uri.toString());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess() {
        userFilterAdapter.refreshAdapter();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showNewItemDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.new_item_dialog, null);
        final EditText newItemView = (EditText) dialogView.findViewById(R.id.newItemTextView);
        newItemView.setHint(R.string.userRuleNewItemDialogHint);

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

    private void showImportDialog() {
        final View dialogLayout = getLayoutInflater().inflate(R.layout.new_item_dialog, null);
        final EditText view = (EditText) dialogLayout.findViewById(R.id.newItemTextView);

        final PreferencesService preferencesService = ServiceLocator.getInstance(this).getPreferencesService();
        final String lastImportUrl = preferencesService.getLastImportUrl();
        if (lastImportUrl != null) {
            view.setText(lastImportUrl);
        }

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Editable text = view.getText();

                final String url = text.toString();
                if (isReadableFile(url) || validateUrl(url)) {
                    ServiceLocator.getInstance(UserFilterActivity.this).getFilterService().importUserRulesFromUrl(UserFilterActivity.this, url);
                    preferencesService.setLastImportUrl(url);
                    text.clear();
                    dialog.dismiss();
                } else {
                    view.setError(getString(R.string.importUserRulesUrlErrorMessage));
                }
            }

            private boolean validateUrl(String url) {
                return url != null && Patterns.WEB_URL.matcher(url).matches();
            }

            private boolean isReadableFile(String path) {
                File f = new File(path);
                return f.exists() && f.isFile() && f.canRead();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog)
                .setTitle(R.string.importUserRulesDialogTitle)
                .setView(dialogLayout)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, null);

        // Android version is 4.4 or higher
        if (Build.VERSION.SDK_INT >= 19) {
            DialogInterface.OnClickListener browseListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = new Intent();
                        intent.setType("*/*");
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, REQUEST_CODE);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), R.string.progressGenericErrorText, Toast.LENGTH_LONG).show();
                    }
                }
            };

            builder.setNeutralButton(R.string.browseButtonText, browseListener);
        }
        builder.show();
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
            preferencesService.addUserRuleItem(StringUtils.trim(rule));

            new ApplyAndRefreshTask(filterService, activity).execute();

            refreshAdapter();
        }

        public void removeRule(String rule) {
            preferencesService.removeUserRuleItem(StringUtils.trim(rule));

            new ApplyAndRefreshTask(filterService, activity).execute();

            refreshAdapter();
        }

        public void clearRules() {
            preferencesService.clearUserRules();

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
