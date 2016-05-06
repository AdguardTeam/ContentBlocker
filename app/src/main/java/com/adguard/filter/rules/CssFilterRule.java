package com.adguard.filter.rules;

import org.apache.commons.lang.StringUtils;

/**
 * Rule for adding CSS filters to page.
 * <br/>
 * There are two css rules type: black-list css rule, white-list css rule.
 * <br/>
 * <br/>
 * ------- Black list rule text format -------
 * <br/>
 * [domains]##[css selector]
 * <br/>
 * domains - coma-separated list, specifies domains where this CSS rule should be used.
 * if [domains] is not set - rule is used for all domains.
 * You can specify ~domainname1 to disable domainname1.
 * <br/>
 * <br/>
 * ------- White list rule text format -------
 * <br/>
 * [domains]#@#[css selector]
 * <br/>
 * domains - coma-separated list, specifies domains where target CSS rule should be used.
 * target rule css selector - css selector from rule to which this exception
 * <p/>
 * CSS inject rule type
 * <p/>
 * Rule for inject CSS style to page.
 * Exist two css inject rules type: black-list css rule, white-list css rule.
 * <p/>
 * ------- Black list rule text format -------
 * [domains]#$#[css selector] {css style}
 * domains - coma-separated list, specifies domains where this CSS inject rule should be used.
 * if [domains] is not set - rule is used for all domains.
 * You can specify ~domainname1 to disable domainname1.
 * <p/>
 * ------- White list rule text format -------
 * [domains]#@$#[css selector] {css style}
 * domains - coma-separated list, specifies domains where target CSS rule should be used.
 * css selector - css selector from rule to which this exception applied
 * css style - css selector from rule to which this exception applieds
 */
public class CssFilterRule extends FilterRule {

    private final String cssContent;
    private final boolean styleInject;
    private final boolean whiteListRule;

    /**
     * Creates CSS filter rule
     *
     * @param ruleText Rule text
     */
    public CssFilterRule(String ruleText) {
        super(ruleText);

        String mask;
        boolean styleInject = false;
        boolean whiteListRule = false;
        if (StringUtils.contains(ruleText, MASK_CSS_INJECT_EXCEPTION_RULE)) {
            mask = MASK_CSS_INJECT_EXCEPTION_RULE;
            whiteListRule = true;
            styleInject = true;
        } else if (StringUtils.contains(ruleText, MASK_CSS_INJECT_RULE)) {
            mask = MASK_CSS_INJECT_RULE;
            styleInject = true;
        } else if (StringUtils.contains(ruleText, MASK_CSS_EXCEPTION_RULE)) {
            mask = MASK_CSS_EXCEPTION_RULE;
            whiteListRule = true;
        } else if (StringUtils.contains(ruleText, MASK_CSS_RULE)) {
            mask = MASK_CSS_RULE;
        } else {
            throw new IllegalArgumentException("ruleText");
        }

        int indexOfMask = StringUtils.indexOf(ruleText, mask);
        if (indexOfMask > 0) {
            // domains are specified, parsing
            String domains = StringUtils.substring(ruleText, 0, indexOfMask);
            loadDomains(domains);
        }

        this.styleInject = styleInject;
        this.whiteListRule = whiteListRule;
        cssContent = ruleText.substring(indexOfMask + mask.length());
    }

    /**
     * Css selector for blocked element (for standard css-rule)
     * or css style for css-injection rules.
     *
     * @return Css content
     */
    public String getCssContent() {
        return cssContent;
    }

    /**
     * If true - this is white-list css rule.
     *
     * @return true for white-list css rules.
     */
    public boolean isWhiteListRule() {
        return whiteListRule;
    }

    /**
     * @return true if this is style injection rule
     */
    public boolean isStyleInject() {
        return styleInject;
    }
}