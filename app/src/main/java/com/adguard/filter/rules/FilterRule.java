package com.adguard.filter.rules;

import com.adguard.commons.collections.Lists;
import com.adguard.commons.web.UrlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base class for all filter rules
 */
public abstract class FilterRule {

    public static final int MIN_RULE_LENGTH = 4;
    public static final String PARAMETER_START = "[";
    public static final String PARAMETER_END = "]";
    public static final String MASK_WHITE_LIST = "@@";
    public static final String MASK_CONTENT_RULE = "$$";
    public static final String MASK_CSS_RULE = "##";
    public static final String MASK_CSS_EXCEPTION_RULE = "#@#";
    public static final String MASK_CSS_INJECT_RULE = "#$#";
    public static final String MASK_CSS_INJECT_EXCEPTION_RULE = "#@$#";
    public static final String MASK_SCRIPT_RULE = "#%#";
    public static final String COMMENT = "!";
    public static final String EQUAL = "=";
    public static final char COMA_DELIMITER = ',';
    public static final char LINE_DELIMITER = '|';
    public static final String NOT_MARK = "~";
    public static final String MASK_OBSOLETE_SCRIPT_INJECTION = "###adg_start_script_inject";
    public static final String MASK_OBSOLETE_STYLE_INJECTION = "###adg_start_style_inject";
    public static final String META_START = "[";

    private final String ruleText;
    private List<String> permittedDomains;
    private List<String> restrictedDomains;

    /**
     * Creates FilterRule text
     *
     * @param ruleText Rule text
     */
    protected FilterRule(String ruleText) {
        if (StringUtils.isEmpty(ruleText)) {
            throw new IllegalArgumentException("ruleText cannot be empty");
        }

        this.ruleText = ruleText;
    }

    /**
     * Returns true if rule is CSS, JS or Content
     *
     * @param ruleText Rule text
     * @return true if rule is CSS, JS or Content
     */
    public static boolean isCosmeticRule(String ruleText) {
        return StringUtils.isEmpty(ruleText) ||
                ruleText.contains(MASK_CSS_RULE) ||
                ruleText.contains(MASK_CSS_EXCEPTION_RULE) ||
                ruleText.contains(MASK_CSS_INJECT_RULE) ||
                ruleText.contains(MASK_CSS_INJECT_EXCEPTION_RULE) ||
                ruleText.contains(MASK_SCRIPT_RULE) ||
                ruleText.contains(MASK_CONTENT_RULE);
    }

    /**
     * Creates filter rule.
     * If this rule text is not valid - returns null.
     *
     * @param ruleText Rule text
     * @return Filter rule of the proper type
     */
    public static FilterRule createRule(String ruleText) {

        ruleText = StringUtils.trim(ruleText);

        if (StringUtils.isBlank(ruleText) ||
                StringUtils.length(ruleText) < MIN_RULE_LENGTH ||
                StringUtils.startsWith(ruleText, COMMENT) ||
                StringUtils.startsWith(ruleText, META_START) ||
                StringUtils.contains(ruleText, MASK_OBSOLETE_SCRIPT_INJECTION) ||
                StringUtils.contains(ruleText, MASK_OBSOLETE_STYLE_INJECTION)) {
            return null;
        }

        try {
            if (StringUtils.startsWith(ruleText, MASK_WHITE_LIST)) {
                return new UrlFilterRule(ruleText);
            }

            if (StringUtils.contains(ruleText, MASK_CONTENT_RULE)) {
                return new ContentFilterRule(ruleText);
            }

            if (StringUtils.contains(ruleText, MASK_CSS_RULE) ||
                    StringUtils.contains(ruleText, MASK_CSS_EXCEPTION_RULE) ||
                    StringUtils.contains(ruleText, MASK_CSS_INJECT_RULE) ||
                    StringUtils.contains(ruleText, MASK_CSS_INJECT_EXCEPTION_RULE)) {
                return new CssFilterRule(ruleText);
            }

            if (StringUtils.contains(ruleText, MASK_SCRIPT_RULE)) {
                return new ScriptFilterRule(ruleText);
            }

            return new UrlFilterRule(ruleText);
        } catch (Exception ex) {
            LoggerFactory.getLogger(FilterRule.class).warn("Error creating filter rule {}:\r\n{}", ruleText, ex);
            return null;
        }
    }

    /**
     * Gets rule text
     *
     * @return Rule text
     */
    public String getRuleText() {
        return ruleText;
    }

    /**
     * Gets list of domains this rule is permitted on
     *
     * @return List of permitted domains
     */
    public List<String> getPermittedDomains() {
        return permittedDomains;
    }

    /**
     * Gets list of domains this rule is restricted on
     *
     * @return List of restricted domains
     */
    public List<String> getRestrictedDomains() {
        return restrictedDomains;
    }

    /**
     * Generic filter rules count as rules that:
     * <br/>
     * 1. Do not have a domain specified. "Hide this element on all domains"
     * <br/>
     * 2. Have only domain exceptions specified. "Hide this element on all domains except example.com"
     * <br/>
     * ~example.com##.ad
     * ||example.com^$third-party
     *
     * @return true if this rule is generic
     */
    public boolean isGeneric() {
        return CollectionUtils.isEmpty(permittedDomains);
    }

    /**
     * Checks if this rule is domain sensitive or not
     *
     * @return true if rule has permitted or restricted domains
     */
    public boolean isDomainSensitive() {
        return CollectionUtils.isNotEmpty(permittedDomains) ||
                CollectionUtils.isNotEmpty(restrictedDomains);
    }

    /**
     * Checks if this rule is permitted for the specified domain
     *
     * @param domainName Domain name
     * @return true if rule is permitted
     */
    public boolean isPermitted(String domainName) {
        if (StringUtils.isEmpty(domainName)) {
            return false;
        }

        if (UrlUtils.isDomainOrSubDomain(domainName, restrictedDomains)) {
            return false;
        }

        //noinspection SimplifiableIfStatement
        if (CollectionUtils.isNotEmpty(permittedDomains)) {
            // If permitted domains set -- this rule work for permitted domains ONLY
            return UrlUtils.isDomainOrSubDomain(domainName, permittedDomains);
        }

        return true;
    }

    /**
     * Loads PermittedDomains and RestrictedDomains collections
     * from the rule text
     *
     * @param domains Domains part of the rule text
     */
    protected void loadDomains(String domains) {
        if (StringUtils.isEmpty(domains)) {
            return;
        }

        String[] parts = StringUtils.split(domains, new String(new char[]{COMA_DELIMITER, LINE_DELIMITER}));

        try {
            for (String domain : parts) {
                if (StringUtils.startsWith(domain, NOT_MARK)) {
                    String domainName = toPunycode(domain.substring(1).trim());
                    if (StringUtils.isNotEmpty(domainName)) {
                        addRestrictedDomain(domainName);
                    }
                } else {
                    String domainName = toPunycode(domain.trim());
                    if (StringUtils.isNotEmpty(domainName)) {
                        addPermittedDomain(domainName);
                    }
                }
            }
        } catch (Exception ex) {
            LoggerFactory.getLogger(this.getClass()).error("Error while loading domains from " + domains, ex);
        }
    }

    /**
     * Removes specified domain from the list of permitted.
     *
     * @param domainName Domain to remove
     * @return true if domain was removed successfully
     */
    public boolean removePermittedDomain(String domainName) {
        return Lists.remove(permittedDomains, domainName);
    }

    /**
     * Removes specified domains from the list of permitted
     *
     * @param domainNames Collection of domain names to remove
     */
    public void removePermittedDomains(Collection<String> domainNames) {
        Lists.removeAll(permittedDomains, domainNames);
    }

    /**
     * Adds specified domain name to the list of permitted domains
     *
     * @param domainName Domain to add
     */
    public void addPermittedDomain(String domainName) {
        if (StringUtils.isEmpty(domainName)) {
            return;
        }
        if (permittedDomains == null) {
            permittedDomains = new ArrayList<>();
        }
        permittedDomains.add(domainName);
    }

    /**
     * Adds specified domains to the list of permitted domains
     *
     * @param domainNames Domains to add
     */
    public void addPermittedDomains(Collection<String> domainNames) {
        if (domainNames == null) {
            return;
        }
        for (String domainName : domainNames) {
            addPermittedDomain(domainName);
        }
    }

    /**
     * Removes the specified domain from the list of restricted
     *
     * @param domainName Domain to remove
     * @return true if domain has been removed
     */
    public boolean removeRestrictedDomain(String domainName) {
        return Lists.remove(restrictedDomains, domainName);
    }

    /**
     * Removes specified domains from the list of restricted
     *
     * @param domainNames Domains to remove
     */
    public void removeRestrictedDomains(Collection<String> domainNames) {
        Lists.removeAll(restrictedDomains, domainNames);
    }

    /**
     * Adds specified domain name to the list of restricted domains
     *
     * @param domainName Domain name to restrict
     */
    public void addRestrictedDomain(String domainName) {
        if (StringUtils.isEmpty(domainName)) {
            return;
        }
        if (restrictedDomains == null) {
            restrictedDomains = new ArrayList<>();
        }
        restrictedDomains.add(domainName);
    }

    /**
     * Adds specified domains to the list of restricted domains
     *
     * @param domainNames Domain names too restict
     */
    public void addRestrictedDomains(Collection<String> domainNames) {
        if (domainNames == null) {
            return;
        }
        for (String domainName : domainNames) {
            addRestrictedDomain(domainName);
        }
    }

    @Override
    public String toString() {
        return getRuleText();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterRule that = (FilterRule) o;
        String ruleText = getRuleText();
        return !(ruleText != null ? !ruleText.equals(that.getRuleText()) : that.getRuleText() != null);
    }

    @Override
    public int hashCode() {
        return getRuleText() != null ? getRuleText().hashCode() : 0;
    }

    /**
     * Transforms domain name to punycode if needed
     *
     * @param domainName Domain name
     * @return Domain name in punycode
     */
    private static String toPunycode(String domainName) {
        if (UrlUtils.isASCII(domainName)) {
            return domainName;
        }

        return UrlUtils.toPunycode(domainName);
    }
}
