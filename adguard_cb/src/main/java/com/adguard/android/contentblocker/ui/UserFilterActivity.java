/**
 * This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 * Copyright © 2018 AdGuard Content Blocker. All rights reserved.
 * <p>
 * AdGuard Content Blocker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * <p>
 * AdGuard Content Blocker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.contentblocker.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ui.utils.AlertDialogUtils;
import com.adguard.android.contentblocker.ui.utils.ApplyAndRefreshTask;
import com.adguard.android.contentblocker.ui.utils.FilterRulesAdapter;
import com.adguard.android.contentblocker.service.FilterService;
import com.adguard.android.contentblocker.service.FilterServiceImpl;
import com.adguard.android.contentblocker.service.PreferencesService;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Set;

public class UserFilterActivity extends AppCompatActivity implements FilterServiceImpl.OnImportListener {

    private static final int REQUEST_CODE = 1237;

    private FilterService filterService;
    private FilterRulesAdapter userFilterAdapter;
    private FloatingActionButton addUserRuleFloatingButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_filter);

        setupActionBar();

        filterService = ServiceLocator.getInstance(this).getFilterService();
        userFilterAdapter = new UserFilterRulesAdapter(this, filterService.getUserRulesItems(), filterService.getDisabledUserRules());

        View emptyListWrapper = findViewById(R.id.userfilterEmptyWrapper);
        ListView userFilterList = findViewById(R.id.user_filter_list);
        userFilterList.setEmptyView(emptyListWrapper);
        userFilterList.setAdapter(userFilterAdapter);
        userFilterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String rule = userFilterAdapter.getItem(position);
                showNewOrEditItemDialog(rule, position);
            }
        });

        findViewById(R.id.user_filter_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewOrEditItemDialog(null, 0);
            }
        });

        findViewById(R.id.user_filter_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportDialog();
            }
        });

        addUserRuleFloatingButton = findViewById(R.id.addUserRule);
        addUserRuleFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewOrEditItemDialog(null, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFloatingButton();
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
                showNewOrEditItemDialog(null, 0);
                break;
            case R.id.user_filter_import:
                showImportDialog();
                break;
            case R.id.user_filter_clear:
                AlertDialogUtils.confirm(this,
                        R.string.warning,
                        R.string.confirmClearUserFilterMessage,
                        new AlertDialogUtils.DefaultConfirmationListener() {
                            @Override
                            public void ok() {
                                filterService.clearUserRules();
                                updateAdapter();
                            }
                        });

                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                ServiceLocator.getInstance(this).getFilterService().importUserRulesFromUrl(this, uri.toString());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess() {
        updateAdapter();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void updateAdapter() {
        List<String> userRules = filterService.getUserRulesItems();
        Set<String> disabledItems = filterService.getDisabledUserRules();
        userFilterAdapter.reload(userRules, disabledItems);
    }

    @SuppressLint("InflateParams")
    private void showNewOrEditItemDialog(final String rule, final int position) {
        View dialogLayout = getLayoutInflater().inflate(R.layout.new_item_dialog, null);
        final EditText editTextView = dialogLayout.findViewById(R.id.newItemTextView);
        editTextView.setHint(R.string.userRuleNewItemDialogHint);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog)
                .setTitle(R.string.enter_rule_text)
                .setView(dialogLayout)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Editable text = editTextView.getText();
                        if (rule != null) {
                            userFilterAdapter.replace(StringUtils.trim(text.toString()), position);
                        } else {
                            userFilterAdapter.add(StringUtils.trim(text.toString()));
                        }
                        text.clear();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null);

        if (rule != null) {
            editTextView.setText(rule);
            builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    final Editable text = editTextView.getText();
                    text.clear();

                    AlertDialogUtils.confirm(UserFilterActivity.this,
                            R.string.warning,
                            R.string.confirmRemoveUserRuleMessage,
                            new AlertDialogUtils.DefaultConfirmationListener() {
                                @Override
                                public void ok() {
                                    userFilterAdapter.remove(rule);
                                }
                            });
                }
            });
        }

        final AlertDialog dialog = builder.create();
        if (rule != null) {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface d) {
                    int color = ContextCompat.getColor(UserFilterActivity.this, android.R.color.holo_red_dark);
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(color);
                }
            });
        }
        dialog.show();
    }

    @SuppressLint("InflateParams")
    private void showImportDialog() {
        final View dialogLayout = getLayoutInflater().inflate(R.layout.new_item_dialog, null);
        final EditText view = dialogLayout.findViewById(R.id.newItemTextView);

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
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.browseButtonText, new DialogInterface.OnClickListener() {
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
                });
        builder.show();
    }

    private void updateFloatingButton() {
        addUserRuleFloatingButton.setVisibility(userFilterAdapter.getCount() == 0 ? View.GONE : View.VISIBLE);
    }

    private class UserFilterRulesAdapter extends FilterRulesAdapter {

        UserFilterRulesAdapter(Context context, List<String> userRules, Set<String> disabledItems) {
            super(context, userRules, disabledItems);
        }

        @Override
        public void add(String item) {
            super.add(item);
            // Make sure that the rule is not disabled
            filterService.enableUserRule(item, true);
            filterService.addUserRuleItem(item);
            updateFloatingButton();
            new ApplyAndRefreshTask(filterService, UserFilterActivity.this).execute();
        }

        @Override
        public void remove(String item) {
            super.remove(item);
            // We do this just to make sure that there are no ghost records in the disabled rules list
            filterService.enableUserRule(item, true);
            filterService.setUserRules(getText());
            updateFloatingButton();
            new ApplyAndRefreshTask(filterService, UserFilterActivity.this).execute();
        }

        @Override
        public void replace(String item, int index) {
            super.replace(item, index);
            filterService.setUserRules(getText());
            updateFloatingButton();
            new ApplyAndRefreshTask(filterService, UserFilterActivity.this).execute();
        }

        @Override
        public void reload(List<String> values, Set<String> disabledItems) {
            super.reload(values, disabledItems);
            updateFloatingButton();
            new ApplyAndRefreshTask(filterService, UserFilterActivity.this).execute();
        }

        @Override
        protected void setItemChecked(String item, boolean checked) {
            super.setItemChecked(item, checked);
            filterService.enableUserRule(item, checked);
            new ApplyAndRefreshTask(filterService, UserFilterActivity.this).execute();
        }
    }
}
