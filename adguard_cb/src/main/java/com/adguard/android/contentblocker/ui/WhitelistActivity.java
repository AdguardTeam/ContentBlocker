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

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.adguard.android.contentblocker.R;
import com.adguard.android.contentblocker.ServiceLocator;
import com.adguard.android.contentblocker.service.FilterService;
import com.adguard.android.contentblocker.ui.utils.AlertDialogUtils;
import com.adguard.android.contentblocker.ui.utils.ApplyAndRefreshTask;
import com.adguard.android.contentblocker.ui.utils.FilterRulesAdapter;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

public class WhitelistActivity extends AppCompatActivity {
    private FilterService filterService;
    private WhitelistAdapter whitelistAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);

        filterService = ServiceLocator.getInstance(this).getFilterService();

        View emptyWrapper = findViewById(R.id.emptyWrapper);

        List<String> whiteListItems = filterService.getWhiteListItems();
        Set<String> disabledWhitelistRules = filterService.getDisabledWhitelistRules();
        whitelistAdapter = new WhitelistAdapter(whiteListItems, disabledWhitelistRules);

        ListView whiteList = findViewById(R.id.whitelist);
        whiteList.setEmptyView(emptyWrapper);
        whiteList.setAdapter(whitelistAdapter);
        whiteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String domain = whitelistAdapter.getItem(position);
                showNewOrEditItemDialog(domain, position);
            }
        });

        findViewById(R.id.add_domain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewOrEditItemDialog(null, 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_whitelist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_domain:
                showNewOrEditItemDialog(null, 0);
                break;

            case R.id.clear_whitelist:
                AlertDialogUtils.confirm(this,
                        R.string.warning,
                        R.string.confirmClearWhitelistMessage,
                        new AlertDialogUtils.DefaultConfirmationListener() {
                            @Override
                            public void ok() {
                                filterService.clearWhiteList();
                                updateAdapter();
                            }
                        });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateAdapter() {
        List<String> whiteListItems = filterService.getWhiteListItems();
        Set<String> disabledWhitelistRules = filterService.getDisabledWhitelistRules();
        whitelistAdapter.reload(whiteListItems, disabledWhitelistRules);
    }

    @SuppressLint("InflateParams")
    private void showNewOrEditItemDialog(final String domainName, final int position) {
        View dialogLayout = getLayoutInflater().inflate(R.layout.new_item_dialog, null);
        final EditText view = dialogLayout.findViewById(R.id.newItemTextView);
        view.setHint(R.string.whitelistNewItemDialogHint);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setTitle(R.string.whitelistNewItemDialogTitle)
                .setView(dialogLayout)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton(getString(R.string.cancel), null);

        if (domainName != null) {
            view.setText(domainName);
            builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    final Editable text = view.getText();
                    text.clear();
                    AlertDialogUtils.confirm(WhitelistActivity.this, R.string.warning, R.string.confirmRemoveWhitelistDomainMessage,
                            new AlertDialogUtils.DefaultConfirmationListener() {
                                @Override
                                public void ok() {
                                    whitelistAdapter.remove(domainName);
                                }
                            });
                }
            });
        }

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editable text = view.getText();
                        String item = StringUtils.trim(text.toString());

                        if (!validateWhitelistItem(item)) {
                            view.setError(getString(R.string.whitelistNewItemErrorMessage));
                        } else if (filterService.getWhiteListItems().contains(item)) {
                            view.setError(getString(R.string.whitelistNewItemExistsErrorMessage));
                        } else {
                            if (domainName == null) {
                                whitelistAdapter.add(item);
                            } else {
                                whitelistAdapter.replace(item, position);
                            }
                            text.clear();
                            alertDialog.dismiss();
                        }
                    }
                });
                int color = ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light);
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(color);
            }
        });

        alertDialog.show();
    }

    private boolean validateWhitelistItem(String domainName) {
        return domainName != null && Patterns.DOMAIN_NAME.matcher(domainName).matches();
    }

    private class WhitelistAdapter extends FilterRulesAdapter {

        WhitelistAdapter(List<String> whiteList, Set<String> disabledItems) {
            super(WhitelistActivity.this, whiteList, disabledItems);
        }

        @Override
        public void add(String item) {
            super.add(item);
            // Make sure that the rule is not disabled
            filterService.enableWhitelistRule(item, true);
            filterService.addWhitelistItem(item);
            new ApplyAndRefreshTask(filterService, WhitelistActivity.this).execute();
            invalidateOptionsMenu();
        }

        @Override
        public void remove(String item) {
            super.remove(item);
            // We do this just to make sure that there are no ghost records in the disabled rules list
            filterService.enableWhitelistRule(item, true);
            filterService.setWhiteList(getText());
            new ApplyAndRefreshTask(filterService, WhitelistActivity.this).execute();
            invalidateOptionsMenu();
        }

        public void replace(String item, int index) {
            super.remove(getItem(index));
            super.insert(item, index);
            filterService.setWhiteList(getText());
            new ApplyAndRefreshTask(filterService, WhitelistActivity.this).execute();
            invalidateOptionsMenu();
        }

        @Override
        protected void setItemChecked(String item, boolean checked) {
            super.setItemChecked(item, checked);
            filterService.enableWhitelistRule(item, checked);
            new ApplyAndRefreshTask(filterService, WhitelistActivity.this).execute();
        }
    }
}
