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

import org.apache.commons.lang3.StringUtils;

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
