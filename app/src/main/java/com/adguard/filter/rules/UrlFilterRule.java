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

import com.adguard.commons.lang.StringHelperUtils;
import com.adguard.commons.web.UrlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Rule for blocking requests to URLs.<br/>
 * Rule text format is described here: http://adguard.com/en/filterrules.html
 */
public class UrlFilterRule extends FilterRule {

    public static final int SHORTCUT_LENGTH = 6;
    public static final String OPTIONS_DELIMITER = "$";
    public static final String DOMAIN_OPTION = "domain";
    public static final String THIRD_PARTY_OPTION = "third-party";
    public static final String MATCH_CASE_OPTION = "match-case";
    public static final String DOCUMENT_OPTION = "document";
    public static final String ELEMHIDE_OPTION = "elemhide";
    public static final String URLBLOCK_OPTION = "urlblock";
    public static final String JSINJECT_OPTION = "jsinject";
    public static final String CONTENT_OPTION = "content";
    public static final String GENERIC_HIDE_OPTION = "generichide";
    public static final String GENERIC_BLOCK_OPTION = "genericblock";
    public static final String POPUP_OPTION = "popup";
    public static final String MP4_OPTION = "mp4";
    public static final String EMPTY_OPTION = "empty";

    public static final String CONTENT_SCRIPT_OPTION = "script";
    public static final String CONTENT_IMAGE_OPTION = "image";
    public static final String CONTENT_STYLESHEET_OPTION = "stylesheet";
    public static final String CONTENT_OBJECT_OPTION = "object";
    public static final String CONTENT_SUBDOCUMENT_OPTION = "subdocument";
    public static final String CONTENT_XMLHTTPREQUEST_OPTION = "xmlhttprequest";
    public static final String CONTENT_OBJECT_SUBREQUEST = "object-subrequest";
    public static final String CONTENT_MEDIA_OPTION = "media";
    public static final String CONTENT_FONT_OPTION = "font";
    public static final String CONTENT_OTHER_OPTION = "other";

    public static final String MASK_REGEX_RULE = "/";
    public static final String MASK_START_URL = "||";
    public static final String MASK_PIPE = "|";
    public static final String MASK_ANY_SYMBOL = "*";
    public static final String MASK_SEPARATOR = "^";
    public static final String REGEXP_START_URL = "^https?://([a-z0-9-_.]+\\.)?";
    public static final String REGEXP_ANY_SYMBOL = ".*";
    public static final String REGEXP_START_STRING = "^";
    public static final String REGEXP_SEPARATOR = "([^ a-zA-Z0-9.%]|$)";
    public static final String REGEXP_END_STRING = "$";

    private static final List<String> IGNORED_OPTIONS =
            Arrays.asList("collapse",
                    "~collapse",
                    "background",
                    "~background",
                    "~document");

    private boolean whiteListRule;
    private boolean documentLevelRule;

    private int permittedContentTypesMask = ContentType.ANY.getFlagValue();
    private int restrictedContentTypesMask;
    private EnumSet<UrlFilterRuleOption> enabledOptions;
    private EnumSet<UrlFilterRuleOption> disabledOptions;

    private String shortcut;
    private Pattern urlRegexp;
    private String regex;
    private boolean invalidRule;

    /**
     * Creates url filter rule
     *
     * @param ruleText Rule text
     */
    public UrlFilterRule(String ruleText) {
        super(ruleText);

        String urlRuleText = ruleText;

        if (StringUtils.startsWith(urlRuleText, MASK_WHITE_LIST)) {
            urlRuleText = urlRuleText.substring(MASK_WHITE_LIST.length());
            whiteListRule = true;
        }

        int optionsIndex = StringUtils.lastIndexOf(urlRuleText, OPTIONS_DELIMITER);
        if (optionsIndex > -1) {
            urlRuleText = urlRuleText.substring(0, optionsIndex);
        }

        // Transform to punycode
        urlRuleText = toPunycode(urlRuleText);

        // More about regex rules http://jira.performix.ru/browse/AG-6604
        boolean regexRule = urlRuleText.startsWith(MASK_REGEX_RULE) && urlRuleText.endsWith(MASK_REGEX_RULE);
        if (!regexRule) {
            // Searching for shortcut for normal rules
            shortcut = findShortcut(urlRuleText);
        }
    }

    /**
     * Checks request url against this filter rule.
     * Detects content type from the URL.
     *
     * @param requestUrl Request URL
     * @param thirdParty true for third-party request
     * @return true if request is filtered
     */
    public boolean isFiltered(String requestUrl, boolean thirdParty) {
        return isFiltered(requestUrl, thirdParty, EnumSet.of(ContentType.detectContentType(requestUrl)));
    }

    /**
     * Checks request url against filter
     *
     * @param requestUrl   Request url
     * @param thirdParty   Is request third party or not
     * @param contentTypes Request content types mask
     * @return true if request url matches this rule
     */
    public boolean isFiltered(String requestUrl, boolean thirdParty, EnumSet<ContentType> contentTypes) {
        // Lazy loading rule properties
        loadRuleProperties();

        if (isOptionEnabled(UrlFilterRuleOption.THIRD_PARTY) && !thirdParty) {
            // Rule is with $third-party modifier but request is not third party
            return false;
        }

        if (isOptionDisabled(UrlFilterRuleOption.THIRD_PARTY) && thirdParty) {
            // Rule is with $~third-party modifier but request is third party
            return false;
        }

        if (shortcut != null && !StringHelperUtils.containsIgnoreCaseAscii(requestUrl, shortcut)) {
            return false;
        }

        Pattern pattern = getUrlRegexp();
        if (pattern == null || !pattern.matcher(requestUrl).find()) {
            // Request does not match regexp - returning false
            return false;
        }

        // Last check -- content type
        return matchesContentType(contentTypes);
    }

    @Override
    public List<String> getPermittedDomains() {
        // Lazy loading rule properties
        loadRuleProperties();
        return super.getPermittedDomains();
    }

    /**
     * There are two exceptions for domain permitting in url blocking rules.
     * White list rules must fire when request has no referrer.
     * Also rules without third-party option should fire.
     *
     * @param domainName Domain name
     * @return true if permitted
     */
    @Override
    public boolean isPermitted(String domainName) {
        // Lazy loading rule properties
        loadRuleProperties();

        if (StringUtils.isEmpty(domainName)) {
            // For white list rules to fire when request has no referrer
            if (whiteListRule && CollectionUtils.isEmpty(getPermittedDomains())) {
                return true;
            }

            // Also firing rules when there's no constraint on ThirdParty-FirstParty type
            if (CollectionUtils.isEmpty(getPermittedDomains()) &&
                    !isOptionEnabled(UrlFilterRuleOption.THIRD_PARTY) &&
                    !isOptionDisabled(UrlFilterRuleOption.THIRD_PARTY)) {
                return true;
            }
        }

        return super.isPermitted(domainName);
    }

    /**
     * Returns true if this rule can be applied to DOCUMENT only.
     * Examples: $popup, $elemhide and such.
     * Such rules have higher priority than common rules.
     *
     * @return true for document-level rules
     */
    public boolean isDocumentLevel() {
        loadRuleProperties();
        return documentLevelRule;
    }

    @Override
    public boolean isGeneric() {
        // Overriding generic because UrlFilterRule properties are loaded lazily
        loadRuleProperties();
        return super.isGeneric();
    }

    /**
     * @return true if this is whitelist rule
     */
    public boolean isWhiteListRule() {
        return whiteListRule;
    }

    /**
     * True if this filter should check if request is third- or first-party.
     *
     * @return True if we should check third party property
     */
    public boolean isCheckThirdParty() {
        return isOptionEnabled(UrlFilterRuleOption.THIRD_PARTY) ||
                isOptionDisabled(UrlFilterRuleOption.THIRD_PARTY);
    }

    /**
     * If true - filter is only applied to requests from
     * a different origin that the currently viewed page.
     *
     * @return If true - filter third-party requests only
     */
    public boolean isThirdParty() {
        if (isOptionEnabled(UrlFilterRuleOption.THIRD_PARTY)) {
            return true;
        }

        if (isOptionDisabled(UrlFilterRuleOption.THIRD_PARTY)) {
            return false;
        }

        return false;
    }

    /**
     * If true - do not apply generic UrlFilter rules to the web page.
     *
     * @return true if generic url rules should not be applied.
     */
    public boolean isGenericBlock() {
        return isOptionEnabled(UrlFilterRuleOption.GENERIC_BLOCK);
    }

    /**
     * If true - do not apply generic CSS rules to the web page.
     *
     * @return true if generic CSS rules should not be applied.
     */
    public boolean isGenericHide() {
        return isOptionEnabled(UrlFilterRuleOption.GENERIC_HIDE);
    }

    /**
     * If true -- CssFilter cannot be applied to page
     *
     * @return true if CssFilter cannot be applied to page
     */
    public boolean isElemhide() {
        return isOptionEnabled(UrlFilterRuleOption.ELEMHIDE);
    }

    /**
     * If true -- ContentFilter rules cannot be applied to page matching this rule.
     *
     * @return true if ContentFilter should not be applied to page matching this rule.
     */
    public boolean isContent() {
        return isOptionEnabled(UrlFilterRuleOption.CONTENT);
    }

    /**
     * Does not inject adguard javascript to page
     *
     * @return If true - we do not inject adguard js to page matching this rule
     */
    public boolean isJsInject() {
        return isOptionEnabled(UrlFilterRuleOption.JS_INJECT);
    }

    /**
     * If rule is case sensitive returns true
     *
     * @return true if rule is case sensitive
     */
    public boolean isMatchCase() {
        return isOptionEnabled(UrlFilterRuleOption.MATCH_CASE);
    }

    /**
     * This attribute is only for exception rules. If true - do not use
     * url blocking rules for urls where referrer satisfies this rule.
     *
     * @return If true - do not block requests originated from the page matching this rule.
     */
    public boolean isUrlBlock() {
        return isOptionEnabled(UrlFilterRuleOption.URL_BLOCK);
    }

    /**
     * If BlockPopups is true, than window should be closed
     * instead of returning http status 204.
     *
     * @return true if window should be closed
     */
    public boolean isBlockPopups() {
        return isOptionEnabled(UrlFilterRuleOption.BLOCK_POPUPS);
    }

    /**
     * If mp4 is true than Adguard will return mp4 video stub
     *
     * @return true if $mp4 option is enabled
     */
    public boolean isMp4() {
        return isOptionEnabled(UrlFilterRuleOption.MP4);
    }

    /**
     * If empty is true than Adguard will return empty response
     * when request is blocked by such rule
     *
     * @return true if $empty option is enabled
     */
    public boolean isEmptyResponse() {
        return isOptionEnabled(UrlFilterRuleOption.EMPTY_RESPONSE);
    }

    /**
     * Rule shortcut. Used for contentblocker rule search.
     * Look at ShortcutsLookupTable for details.
     *
     * @return Shortcut
     */
    public String getShortcut() {
        return shortcut;
    }

    /**
     * Url regular expression
     *
     * @return Regexp
     */
    public synchronized Pattern getUrlRegexp() {
        if (invalidRule) {
            return null;
        }

        loadRuleProperties();

        if (urlRegexp == null) {

            int regexOptions = Pattern.DOTALL;
            if (!isOptionEnabled(UrlFilterRuleOption.MATCH_CASE)) {
                regexOptions = regexOptions | Pattern.CASE_INSENSITIVE;
            }
            urlRegexp = Pattern.compile(regex, regexOptions);
            regex = null;
        }

        return urlRegexp;
    }

    /**
     * Loads rule properties lazily
     */
    private synchronized void loadRuleProperties() {
        try {
            if (regex != null || urlRegexp != null || invalidRule) {
                // Rule is already loaded
                return;
            }

            String urlRuleText = getRuleText();

            if (StringUtils.startsWith(urlRuleText, MASK_WHITE_LIST)) {
                urlRuleText = urlRuleText.substring(MASK_WHITE_LIST.length());
            }

            int optionsIndex = StringUtils.lastIndexOf(urlRuleText, OPTIONS_DELIMITER);
            if (optionsIndex > -1) {
                // Options are specified, parsing it
                String optionsBase = urlRuleText;
                urlRuleText = urlRuleText.substring(0, optionsIndex);
                String options = optionsBase.substring(optionsIndex + 1);
                loadOptions(options);
            }

            // Transform to punycode
            urlRuleText = toPunycode(urlRuleText);

            boolean regexRule = urlRuleText.startsWith(MASK_REGEX_RULE) && urlRuleText.endsWith(MASK_REGEX_RULE);
            if (regexRule) {
                regex = urlRuleText.substring(MASK_REGEX_RULE.length(), urlRuleText.length() - MASK_REGEX_RULE.length());
                // Pre-compile regex rules
                Pattern pattern = getUrlRegexp();
                if (pattern == null) {
                    throw new IllegalArgumentException("ruleText");
                }
            } else {
                regex = createRegexFromRule(urlRuleText);
            }
        } catch (Exception ex) {
            LoggerFactory.getLogger(this.getClass()).warn("Invalid filter rule: {}\r\n", getRuleText(), ex);
            invalidRule = true;
        }
    }

    /**
     * Creates regexp from url rule text
     *
     * @param urlRuleText Url rule text
     * @return Regexp
     */
    private String createRegexFromRule(String urlRuleText) {
        // Replacing regex special symbols
        String regexText = StringUtils.replaceEach(urlRuleText,
                new String[]{"?", ".", "+", "[", "]", "(", ")", "{", "}", "#", " ", "\\", "$"},
                new String[]{"\\?", "\\.", "\\+", "\\[", "\\]", "\\(", "\\)", "\\{", "\\}", "\\#", "\\ ", "\\\\", "\\$"});

        regexText = regexText.substring(0, MASK_START_URL.length()) +
                StringUtils.replace(regexText.substring(MASK_START_URL.length(), regexText.length() - 1), "|", "\\|") +
                regexText.substring(regexText.length() - 1);
        // Replacing special url masks
        regexText = StringUtils.replace(regexText, MASK_ANY_SYMBOL, REGEXP_ANY_SYMBOL);
        regexText = StringUtils.replace(regexText, MASK_SEPARATOR, REGEXP_SEPARATOR);
        if (regexText.startsWith(MASK_START_URL)) {
            regexText = REGEXP_START_URL + regexText.substring(MASK_START_URL.length());
        } else if (regexText.startsWith(MASK_PIPE)) {
            regexText = REGEXP_START_STRING + regexText.substring(MASK_PIPE.length());
        }
        if (regexText.endsWith(MASK_PIPE)) {
            regexText = regexText.substring(0, regexText.length() - 1) + REGEXP_END_STRING;
        }

        return regexText;
    }

    /**
     * Checks if request matches rule's content type constraints
     *
     * @param contentTypes Request content types mask
     * @return true if request matches this content type
     */
    private boolean matchesContentType(EnumSet<ContentType> contentTypes) {
        if (permittedContentTypesMask == ContentType.ANY.getFlagValue() &&
                restrictedContentTypesMask == 0) {
            // Rule does not contain any constraint
            return true;
        }

        int contentTypeMask = ContentType.getMask(contentTypes);

        // Checking that either all content types are permitted or request content type is in the permitted list
        boolean matchesPermitted = permittedContentTypesMask == ContentType.ANY.getFlagValue() ||
                (permittedContentTypesMask & contentTypeMask) != 0;

        // Checking that either no content types are restricted or request content type is not in the restricted list
        boolean notMatchesRestricted = restrictedContentTypesMask == 0 ||
                (restrictedContentTypesMask & contentTypeMask) == 0;

        return matchesPermitted && notMatchesRestricted;
    }

    /**
     * Searches for the shortcut of this url mask.
     * Shortcut is the longest part of the mask without special characters:<br/>
     * *,^,|. If not found anything with the length greater or equal to 8 characters -
     * shortcut is not used.
     *
     * @param urlMask Url mask
     */
    private String findShortcut(String urlMask) {
        String longest = StringUtils.EMPTY;

        String[] parts = StringUtils.split(urlMask, MASK_ANY_SYMBOL + MASK_SEPARATOR + MASK_PIPE);

        for (String part : parts) {
            if (part.length() > longest.length()) {
                longest = part;
            }
        }

        if (longest.length() > SHORTCUT_LENGTH) {
            return longest.substring(longest.length() - SHORTCUT_LENGTH).toLowerCase();
        } else {
            return longest.toLowerCase();
        }
    }

    /**
     * Loads filter rule options
     *
     * @param options Options string
     */
    private void loadOptions(String options) {
        String[] optionsParts = StringUtils.split(options, COMA_DELIMITER);

        for (String option : optionsParts) {
            String[] optionsKeyValue = StringUtils.split(option, EQUAL, 2);
            String optionName = optionsKeyValue[0];

            if (optionName.equals(DOMAIN_OPTION)) {
                if (optionsKeyValue.length > 1) {
                    // Load domain option
                    loadDomains(optionsKeyValue[1]);
                }
            } else if (optionName.equals(THIRD_PARTY_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.THIRD_PARTY, true);
            } else if (optionName.equals(NOT_MARK + THIRD_PARTY_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.THIRD_PARTY, false);
            } else if (optionName.equals(ELEMHIDE_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.ELEMHIDE, true);
            } else if (optionName.equals(DOCUMENT_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.ELEMHIDE, true);
                setUrlFilterRuleOption(UrlFilterRuleOption.CONTENT, true);
                setUrlFilterRuleOption(UrlFilterRuleOption.JS_INJECT, true);
                setUrlFilterRuleOption(UrlFilterRuleOption.URL_BLOCK, true);
            } else if (optionName.equals(CONTENT_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.CONTENT, true);
            } else if (optionName.equals(GENERIC_BLOCK_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.GENERIC_BLOCK, true);
            } else if (optionName.equals(GENERIC_HIDE_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.GENERIC_HIDE, true);
            } else if (optionName.equals(MATCH_CASE_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.MATCH_CASE, true);
            } else if (optionName.equals(JSINJECT_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.JS_INJECT, true);
            } else if (optionName.equals(URLBLOCK_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.URL_BLOCK, true);
            } else if (optionName.equals(POPUP_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.BLOCK_POPUPS, true);
            } else if (optionName.equals(MP4_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.MP4, true);
            } else if (optionName.equals(EMPTY_OPTION)) {
                setUrlFilterRuleOption(UrlFilterRuleOption.EMPTY_RESPONSE, true);
            } else if (optionName.equals(CONTENT_SCRIPT_OPTION)) {
                appendPermittedContentType(ContentType.SCRIPT);
            } else if (optionName.equals(CONTENT_IMAGE_OPTION)) {
                appendPermittedContentType(ContentType.IMAGE);
            } else if (optionName.equals(CONTENT_OBJECT_OPTION)) {
                appendPermittedContentType(ContentType.OBJECT);
            } else if (optionName.equals(CONTENT_STYLESHEET_OPTION)) {
                appendPermittedContentType(ContentType.STYLE);
            } else if (optionName.equals(CONTENT_XMLHTTPREQUEST_OPTION)) {
                appendPermittedContentType(ContentType.XML_HTTP_REQUEST);
            } else if (optionName.equals(CONTENT_OBJECT_SUBREQUEST)) {
                appendPermittedContentType(ContentType.OBJECT_SUBREQUEST);
            } else if (optionName.equals(CONTENT_MEDIA_OPTION)) {
                appendPermittedContentType(ContentType.MEDIA);
            } else if (optionName.equals(CONTENT_FONT_OPTION)) {
                appendPermittedContentType(ContentType.FONT);
            } else if (optionName.equals(CONTENT_SUBDOCUMENT_OPTION)) {
                appendPermittedContentType(ContentType.DOCUMENT);
            } else if (optionName.equals(CONTENT_OTHER_OPTION)) {
                appendPermittedContentType(ContentType.OTHER);
            } else if (optionName.equals(NOT_MARK + CONTENT_SCRIPT_OPTION)) {
                appendRestrictedContentType(ContentType.SCRIPT);
            } else if (optionName.equals(NOT_MARK + CONTENT_IMAGE_OPTION)) {
                appendRestrictedContentType(ContentType.IMAGE);
            } else if (optionName.equals(NOT_MARK + CONTENT_OBJECT_OPTION)) {
                appendRestrictedContentType(ContentType.OBJECT);
            } else if (optionName.equals(NOT_MARK + CONTENT_STYLESHEET_OPTION)) {
                appendRestrictedContentType(ContentType.STYLE);
            } else if (optionName.equals(NOT_MARK + CONTENT_XMLHTTPREQUEST_OPTION)) {
                appendRestrictedContentType(ContentType.XML_HTTP_REQUEST);
            } else if (optionName.equals(NOT_MARK + CONTENT_OBJECT_SUBREQUEST)) {
                appendRestrictedContentType(ContentType.OBJECT_SUBREQUEST);
            } else if (optionName.equals(NOT_MARK + CONTENT_MEDIA_OPTION)) {
                appendRestrictedContentType(ContentType.MEDIA);
            } else if (optionName.equals(NOT_MARK + CONTENT_FONT_OPTION)) {
                appendRestrictedContentType(ContentType.FONT);
            } else if (optionName.equals(NOT_MARK + CONTENT_SUBDOCUMENT_OPTION)) {
                appendRestrictedContentType(ContentType.DOCUMENT);
            } else if (optionName.equals(NOT_MARK + CONTENT_OTHER_OPTION)) {
                appendRestrictedContentType(ContentType.OTHER);
            } else if (!IGNORED_OPTIONS.contains(optionName)) {
                throw new IllegalArgumentException("Unknown option " + optionName);
            }
        }

        // Rules of this types can be applied to documents only
        // $jsinject, $elemhide, $urlblock, $content, $genericblock, $generichide for whitelist rules.
        // $popup - for url blocking
        if (enabledOptions != null && (enabledOptions.contains(UrlFilterRuleOption.JS_INJECT) ||
                enabledOptions.contains(UrlFilterRuleOption.ELEMHIDE) || enabledOptions.contains(UrlFilterRuleOption.URL_BLOCK) ||
                enabledOptions.contains(UrlFilterRuleOption.CONTENT) || enabledOptions.contains(UrlFilterRuleOption.BLOCK_POPUPS))) {
            permittedContentTypesMask = ContentType.DOCUMENT.getFlagValue();
            documentLevelRule = true;
        }
    }

    /**
     * Appends new content type value to permitted list (depending on the current permitted content types)
     *
     * @param contentType Content type to append
     */
    private void appendPermittedContentType(ContentType contentType) {
        if (permittedContentTypesMask == ContentType.ANY.getFlagValue()) {
            permittedContentTypesMask = contentType.getFlagValue();
        } else {
            permittedContentTypesMask |= contentType.getFlagValue();
        }
    }

    /**
     * Appends new content type to restricted list (depending on the current restricted content types)
     *
     * @param contentType Content type to append
     */
    private void appendRestrictedContentType(ContentType contentType) {
        if (restrictedContentTypesMask == 0) {
            restrictedContentTypesMask = contentType.getFlagValue();
        } else {
            restrictedContentTypesMask |= contentType.getFlagValue();
        }
    }

    /**
     * Sets UrlFilterRuleOption
     *
     * @param option  Option
     * @param enabled Enabled or not
     */
    private void setUrlFilterRuleOption(UrlFilterRuleOption option, boolean enabled) {

        if (enabled) {
            if (enabledOptions == null) {
                enabledOptions = EnumSet.of(option);
            } else {
                enabledOptions.add(option);
            }
        } else {
            if (disabledOptions == null) {
                disabledOptions = EnumSet.of(option);
            } else {
                disabledOptions.add(option);
            }
        }
    }

    /**
     * Checks if specified option is enabled
     *
     * @param option Option to check
     * @return true if enabled
     */
    private boolean isOptionEnabled(UrlFilterRuleOption option) {
        loadRuleProperties();
        return enabledOptions != null && enabledOptions.contains(option);
    }

    /**
     * Checks if specified option is disabled
     *
     * @param option Option to check
     * @return true if disabled
     */
    private boolean isOptionDisabled(UrlFilterRuleOption option) {
        loadRuleProperties();
        return disabledOptions != null && disabledOptions.contains(option);
    }

    /**
     * Searches for domain name in rule text and transforms it to punycode if needed.
     *
     * @param ruleText Rule text
     * @return String
     */
    private static String toPunycode(String ruleText) {
        try {
            if (UrlUtils.isASCII(ruleText)) {
                return ruleText;
            }

            String[] startsWith = new String[]{"http://www.", "https://www.", "http://", "https://", "||"};
            String[] contains = new String[]{"/", "^"};
            int startIndex = -1;

            for (String start : startsWith) {
                if (ruleText.startsWith(start)) {
                    startIndex = start.length();
                    break;
                }
            }

            if (startIndex == -1) {
                return ruleText;
            }

            int symbolIndex = -1;
            for (String contain : contains) {
                int index = ruleText.indexOf(contain, startIndex);
                if (index >= 0) {
                    symbolIndex = index;
                    break;
                }
            }

            String domain = symbolIndex == -1
                    ? ruleText.substring(startIndex)
                    : ruleText.substring(startIndex, symbolIndex);

            // In case of one domain
            ruleText = StringUtils.replace(ruleText, domain, UrlUtils.toPunycode(domain));
            return ruleText;
        } catch (Exception ex) {
            LoggerFactory.getLogger(UrlFilterRule.class).warn("Error while getting ascii domain for rule " + ruleText, ex);
            return StringUtils.EMPTY;
        }
    }

    /**
     * URL filter option
     */
    private enum UrlFilterRuleOption {

        /**
         * $elemhide modifier.
         * it makes sense to use this parameter for exceptions only.
         * It prohibits element hiding rules on pages affected by the current rule.
         * Element hiding rules will be described below.
         */
        ELEMHIDE,

        /**
         * limitation on third-party and own requests.
         * If the third-party parameter is used, the rule is applied only to requests
         * coming from external sources. Similarly, ~third-party restricts the rule
         * to requests from the same source that the page comes from. Let’s use an example.
         * The ||domain.com$third-party rule is applied to all sites, except domain.com
         * itself. If we rewrite it as ||domain.com$~third-party, it will be applied
         * only to domain.com, but will not work on other sites.
         */
        THIRD_PARTY,

        /**
         * it makes sense to use this parameter for exceptions only.
         * It prohibits HTML filtration rules on pages affected by the current rule.
         * HTML filtration rules will be described below.
         */
        CONTENT,

        /**
         * If this option is enabled, Adguard won't apply generic CSS rules to the web page.
         */
        GENERIC_HIDE,

        /**
         * If this option is enabled, Adguard won't apply generic UrlFilter rules to the web page.
         */
        GENERIC_BLOCK,

        /**
         * it makes sense to use this parameter for exceptions only.
         * It prohibits the injection of javascript code to web pages.
         * Javascript code is added for blocking banners by size and for
         * the proper operation of Adguard Assistant
         */
        JS_INJECT,

        /**
         * It makes sense to use this parameter for exceptions only.
         * It prohibits the blocking of requests from pages
         * affected by the current rule.
         */
        URL_BLOCK,

        /**
         * For any address matching a&nbsp;blocking rule with this option
         * Adguard will try to&nbsp;automatically close the browser tab.
         */
        BLOCK_POPUPS,

        /**
         * For any address matching blocking rule with this option
         * Adguard will return empty response (204 OK)
         */
        EMPTY_RESPONSE,

        /**
         * defines a rule applied only to addresses with exact letter case matches.
         * For example, /BannerAd.gif$match-case will block http://example.com/BannerAd.gif,
         * but not http://example.com/bannerad.gif.
         * By default, the letter case is not matched.
         */
        MATCH_CASE,

        /**
         * If this option is enabled Adguard will return mp4 stub video
         * for any address matching this rule
         */
        MP4
    }
}
