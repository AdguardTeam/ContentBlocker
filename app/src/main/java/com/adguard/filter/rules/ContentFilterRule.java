/**
 This file is part of Adguard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2016 Performix LLC. All rights reserved.

 Adguard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 Adguard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 Adguard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.filter.rules;

import com.adguard.commons.lang.Wildcard;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Rule for filtering content.
 * <br/>
 * Rule text format:
 * <br/>
 * [domains]$$tagname[attr1="value1"][attr2="value2"][tag-content="value3"]
 * <br/>
 * <strong>domains</strong> coma-separated list, specifies domains where this rule should be used.
 * <br/>
 * if [domains] is not set - rule is used for all domains.
 * <br/>
 * You can specify ~domainname1 to disable domainname1.
 * <br/>
 * <strong>tagname</strong> - html tag name
 * <br/>
 * <strong>attr1</strong> - tag attribute name
 * <br/>
 * <strong>value1</strong> - tag attribute value
 * <br/>
 * <strong>tag-content</strong> - specifies mask for content of tag
 * <br/>
 * <strong>value3</strong> - content value
 */
public class ContentFilterRule extends FilterRule {

    public static final String ATTRIBUTE_START_MARK = "[";
    public static final String ATTRIBUTE_END_MARK = "]";
    public static final String QUOTES = "\"";
    public static final String TAG_CONTENT_MASK = "tag-content";
    public static final String WILDCARD_MASK = "wildcard";
    public static final String TAG_CONTENT_MAX_LENGTH = "max-length";
    public static final String TAG_CONTENT_MIN_LENGTH = "min-length";
    public static final String PARENT_ELEMENTS = "parent-elements";
    public static final String PARENT_SEARCH_LEVEL = "parent-search-level";
    public static final int DEFAULT_PARENT_SEARCH_LEVEL = 3;

    private final String tagName;
    private final Map<String, String> attributesFilter = new HashMap<>();
    private String tagContentFilter;
    private int maxLength;
    private int minLength;
    private List<String> parentElements;
    private int parentSearchLevel;
    private Wildcard wildcard;

    /**
     * Creates an instance of the ContentFilterRule from its text format
     *
     * @param ruleText Rule text
     */
    protected ContentFilterRule(String ruleText) {
        super(ruleText);

        parentSearchLevel = DEFAULT_PARENT_SEARCH_LEVEL;

        int contentRuleMarkIndex = StringUtils.indexOf(ruleText, MASK_CONTENT_RULE);
        int ruleStartIndex = StringUtils.indexOf(ruleText, ATTRIBUTE_START_MARK);

        // Cutting tag name from string
        if (ruleStartIndex == -1) {
            tagName = ruleText.substring(contentRuleMarkIndex + MASK_CONTENT_RULE.length());
        } else {
            tagName = ruleText.substring(contentRuleMarkIndex + MASK_CONTENT_RULE.length(), ruleStartIndex);
        }

        // Loading domains (if any))
        if (contentRuleMarkIndex > 0) {
            String domains = ruleText.substring(0, contentRuleMarkIndex);
            loadDomains(domains);
        }

        // Loading attributes filter
        while (ruleStartIndex != -1) {
            int equalityIndex = ruleText.indexOf(EQUAL, ruleStartIndex + 1);
            int quoteStartIndex = ruleText.indexOf(QUOTES, equalityIndex + 1);
            int quoteEndIndex = getQuoteIndex(ruleText, quoteStartIndex + 1);
            if (quoteStartIndex == -1 || quoteEndIndex == -1) {
                break;
            }
            int ruleEndIndex = ruleText.indexOf(ATTRIBUTE_END_MARK, quoteEndIndex + 1);

            String attributeName = ruleText.substring(ruleStartIndex + 1, equalityIndex);
            String attributeValue = ruleText.substring(quoteStartIndex + 1, quoteEndIndex);
            attributeValue = StringUtils.replace(attributeValue, "\"\"", "\"");

            switch (attributeName) {
                case TAG_CONTENT_MASK:
                    tagContentFilter = attributeValue;
                    break;
                case WILDCARD_MASK:
                    wildcard = new Wildcard(attributeValue, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                    break;
                case TAG_CONTENT_MAX_LENGTH:
                    maxLength = NumberUtils.toInt(attributeValue);
                    break;
                case TAG_CONTENT_MIN_LENGTH:
                    minLength = NumberUtils.toInt(attributeValue);
                    break;
                case PARENT_ELEMENTS:
                    parentElements = Arrays.asList(StringUtils.split(attributeValue, ','));
                    break;
                case PARENT_SEARCH_LEVEL:
                    parentSearchLevel = NumberUtils.toInt(attributeValue);
                    break;
                default:
                    attributesFilter.put(attributeName, attributeValue);
                    break;
            }

            if (ruleEndIndex == -1) break;
            ruleStartIndex = ruleText.indexOf(ATTRIBUTE_START_MARK, ruleEndIndex + 1);
        }
    }

    public String getTagName() {
        return tagName;
    }

    String getTagContentFilter() {
        return tagContentFilter;
    }

    Map<String, String> getAttributesFilter() {
        return attributesFilter;
    }

    int getMaxLength() {
        return maxLength;
    }

    int getMinLength() {
        return minLength;
    }

    public List<String> getParentElements() {
        return parentElements;
    }

    public int getParentSearchLevel() {
        return parentSearchLevel;
    }

    public Wildcard getWildcard() {
        return wildcard;
    }

    private static int getQuoteIndex(String text, int startIndex) {
        char nextChar = '"';
        int quoteIndex = startIndex - 2;

        while (nextChar == '"') {
            quoteIndex = text.indexOf(QUOTES, quoteIndex + 2);
            if (quoteIndex == -1) {
                return -1;
            }
            nextChar = text.length() == (quoteIndex + 1) ? '0' : text.charAt(quoteIndex + 1);
        }

        return quoteIndex;
    }
}
