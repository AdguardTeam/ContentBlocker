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
package com.adguard.android.contentblocker.db;

import android.content.Context;
import android.content.res.Resources;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter rules dao implementation using android internal storage
 */
public class FilterRuleDaoImpl implements FilterRuleDao {

    private final Logger log = LoggerFactory.getLogger(FilterRuleDaoImpl.class);
    private final Context context;

    private static final String MASK_CSS_RULE = "##";
    private static final String MASK_CSS_EXCEPTION_RULE = "#@#";
    private static final String MASK_CSS_INJECT_RULE = "#$#";
    private static final String MASK_CSS_INJECT_EXCEPTION_RULE = "#@$#";
    private static final String MASK_SCRIPT_RULE = "#%#";
    private static final String MASK_CONTENT_RULE = "$$";

    /**
     * Creates an instance of the filter rules internal storage
     *
     * @param context Current context
     */
    public FilterRuleDaoImpl(Context context) {
        this.context = context;
    }

    @Override
    public List<String> selectRuleTexts(List<Integer> filterIds, boolean useCosmetics) {
        List<String> rules = SetUniqueList.setUniqueList(new ArrayList<String>());

        for (int filterId : filterIds) {
            rules.addAll(getRules(filterId, useCosmetics));
        }

        return rules;
    }

    @Override
    public void setFilterRules(int filterId, List<String> rules) {
        try {
            String fileName = getOrCreateFilterFile(filterId);
            context.deleteFile(fileName);
            OutputStream outputStream = context.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);

            IOUtils.writeLines(rules, null, outputStream, "UTF-8");
        } catch (Exception ex) {
            log.error("Cannot insert new rules to filter {}:\r\n{}", filterId, ex);
            throw new RuntimeException("Cannot insert new rules to filter " + filterId, ex);
        }
    }

    /**
     * Gets or creates filter file
     *
     * @param filterId Filter identifier
     * @return Filter file name
     */
    private String getOrCreateFilterFile(int filterId) throws IOException {
        String[] files = context.fileList();
        String fileName = "filter_" + filterId;

        if (!ArrayUtils.contains(files, fileName)) {
            initDefaultFilterRules(fileName);
        }

        return fileName;
    }

    /**
     * Adds rules from the specified filter to the list
     *
     * @param filterId Filter ID
     *
     * @return List of rules
     */
    private List<String> getRules(int filterId, boolean useCosmetics) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;

        List<String> rules = new ArrayList<>();
        try {
            String fileName = getOrCreateFilterFile(filterId);

            inputStream = context.getApplicationContext().openFileInput(fileName);
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null) {
                if (useCosmetics || !isCosmeticRule(line)) {
                    rules.add(line);
                }
                line = reader.readLine();
            }
            return rules;
        } catch (Exception ex) {
            log.error("Cannot select rules for filter {}", filterId, ex);
            throw new RuntimeException("Cannot select rules for filter " + filterId, ex);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(inputStreamReader);
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Returns true if rule is CSS, JS or Content
     *
     * @param ruleText Rule text
     * @return true if rule is CSS, JS or Content
     */
    private static boolean isCosmeticRule(String ruleText) {
        return StringUtils.isEmpty(ruleText) ||
                ruleText.contains(MASK_CSS_RULE) ||
                ruleText.contains(MASK_CSS_EXCEPTION_RULE) ||
                ruleText.contains(MASK_CSS_INJECT_RULE) ||
                ruleText.contains(MASK_CSS_INJECT_EXCEPTION_RULE) ||
                ruleText.contains(MASK_SCRIPT_RULE) ||
                ruleText.contains(MASK_CONTENT_RULE);
    }

    /**
     * Initializes file with default filter rules
     *
     * @param fileName File name
     */
    private void initDefaultFilterRules(String fileName) throws IOException {
        log.info("Initializing filter rules file {}", fileName);

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            Resources resources = context.getResources();

            int id = resources.getIdentifier(fileName, "raw", context.getPackageName());
            if (id > 0) {
                context.deleteFile(fileName);
                outputStream = context.getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);

                log.info("Found default filter rules. Writing to the file.");
                inputStream = resources.openRawResource(id);
                IOUtils.copy(inputStream, outputStream);
            }
            log.info("Default filter has been initialized");
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }
}
