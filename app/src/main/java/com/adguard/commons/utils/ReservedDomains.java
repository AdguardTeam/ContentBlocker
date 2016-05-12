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
package com.adguard.commons.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Special class for reserved domains constants.
 * List got from here:
 * https://publicsuffix.org/list/effective_tld_names.dat
 * <p/>
 * Sorted list is generated with TestUrlUtils.testGenerateReservedDomainNames
 */
public class ReservedDomains {

    private static Logger LOG = LoggerFactory.getLogger(ReservedDomains.class);
    private static String[] reservedDomainNames;

    /**
     * Initializes
     */
    private static synchronized void initialize() {
        try {
            if (reservedDomainNames != null) {
                // Double check
                return;
            }

            LOG.info("Initialize ReservedDomains object");
            InputStream inputStream = ReservedDomains.class.getResourceAsStream("/effective_tld_names.dat");
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            List<String> domains = new ArrayList<>();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();

                if (!StringUtils.isBlank(line)) {
                    domains.add(line);
                }
            }

            reservedDomainNames = new String[domains.size()];
            domains.toArray(reservedDomainNames);
            Arrays.sort(reservedDomainNames);

            LOG.info("ReservedDomains object has been initialized");
        } catch (Exception ex) {
            throw new RuntimeException("Cannot initialize reserved domains collection", ex);
        }
    }

    /**
     * Checks if domain name is reserved
     *
     * @param domainName Domain name
     * @return true if reserved
     */
    public static boolean isReservedDomainName(String domainName) {
        if (reservedDomainNames == null) {
            initialize();
        }

        return Arrays.binarySearch(reservedDomainNames, domainName) >= 0;
    }
}
