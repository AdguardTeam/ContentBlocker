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
package com.adguard.android.commons.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Common internet utils
 */
public class InternetUtils {

    private static final Logger LOG = LoggerFactory.getLogger(InternetUtils.class);

    /**
     * Checks if internet is available
     *
     * @return true if available
     */
    public static boolean isInternetAvailable() {
        return resolveIpAddress("google.com") != null;
    }

    /**
     * Resolves IP address by host name
     *
     * @param domainName Host name
     * @return Ip address or null if cannot resolve
     */
    public static String resolveIpAddress(String domainName) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(domainName);

            if (addresses == null || addresses.length == 0) {
                throw new UnknownHostException(domainName);
            }

            // Return first IPv4 found
            for (InetAddress address : addresses) {
                if (address instanceof Inet4Address) {
                    return address.getHostAddress();
                }
            }

            return addresses[0].getHostAddress();
        } catch (Exception ex) {
            LOG.debug("Cannot resolve host {} due to {}", domainName, ex.getClass().getName() + ": " + ex.getMessage());
            return null;
        }
    }
}
