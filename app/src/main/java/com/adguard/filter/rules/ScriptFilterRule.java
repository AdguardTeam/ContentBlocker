package com.adguard.filter.rules;

import org.apache.commons.lang.StringUtils;

/**
 * Special type of rule supporting javascript injections.
 * Example of this rule is:<br/>
 * #%#window.gapi={ plusone: { go: function(){}, render: function(){} }};
 */
public class ScriptFilterRule extends FilterRule {

    private final String scriptText;

    /**
     * Creates FilterRule text
     *
     * @param ruleText Rule text
     */
    protected ScriptFilterRule(String ruleText) {
        super(ruleText);

        int indexOfMask = StringUtils.indexOf(ruleText, MASK_SCRIPT_RULE);

        // Loading domains (if any))
        if (indexOfMask > 0) {
            String domains = ruleText.substring(0, indexOfMask);
            loadDomains(domains);
        }

        scriptText = ruleText.substring(indexOfMask + MASK_SCRIPT_RULE.length());
    }

    /**
     * @return Javascript text
     */
    public String getScriptText() {
        return scriptText;
    }
}
