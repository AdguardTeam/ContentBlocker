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
package com.adguard.android.contentblocker.api;

import com.adguard.android.contentblocker.commons.web.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Special client to communicate with out backend.
 * This client checks if VPN is active at the moment
 * and protects communication socket if so.
 */
public class HttpServiceClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpServiceClient.class);
    private static final int CONNECTION_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 30000; // 30 seconds

    /**
     * Downloads string from the specified url.
     *
     * @param downloadUrl Download url
     * @return String or null
     * @throws IOException
     */
    public static String downloadString(String downloadUrl) throws IOException {
        LOG.debug("Sending HTTP GET request to {}", downloadUrl);

        final String response = UrlUtils.downloadString(downloadUrl, READ_TIMEOUT, CONNECTION_TIMEOUT);
        if (StringUtils.isEmpty(response)) {
            LOG.error("Response for {} is empty", downloadUrl);
            throw new IOException("Response is empty.");
        }

        LOG.debug("Got response:{}", response);
        return response;
    }

    /**
     * Posts request with specified parameters to url.
     *
     * @param uploadUrl URL to send POST request to
     * @param data      POST body
     * @return Response string
     * @throws IOException
     */
    protected static String postData(String uploadUrl, String data) throws IOException {
        LOG.debug("Sending HTTP POST request to {}. Length={}", uploadUrl, StringUtils.length(data));

        final String response = UrlUtils.postRequest(new URL(uploadUrl), data, "utf-8", "application/x-www-form-urlencoded", true, READ_TIMEOUT, CONNECTION_TIMEOUT);
        if (StringUtils.isEmpty(response)) {
            LOG.error("Response for {} is empty", uploadUrl);
            throw new IOException("Response is empty.");
        }

        LOG.debug("Got response: {}", response);
        return response;
    }

}
