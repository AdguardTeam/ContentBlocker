package com.adguard.commons;

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

    /**
     * Resolves IP address by host name
     *
     * @param domainName Host name
     * @return Ip address or null if cannot resolve
     */
    public static String[] resolveIpAddresses(String domainName) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(domainName);

            if (addresses == null || addresses.length == 0) {
                throw new UnknownHostException(domainName);
            }

            String[] results = new String[addresses.length];
            for (int i=0; i<results.length;i++) {
                results[i] = addresses[i].getHostAddress();
            }

            return results;
        } catch (Exception ex) {
            LOG.debug("Cannot resolve host {} due to {}", domainName, ex.getClass().getName() + ": " + ex.getMessage());
            return null;
        }
    }
}
