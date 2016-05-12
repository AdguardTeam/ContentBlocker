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

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper methods for working with charsets
 */
public class CharsetUtils {

    private static final Pattern CODE_PAGE_REGEX = Pattern.compile("cp([-_ ]*)?([0-9]+)", Pattern.CASE_INSENSITIVE);
    private final static Logger log = LoggerFactory.getLogger(CharsetUtils.class);

    /**
     * Default http encoding
     */
    public static final Charset DEFAULT_HTTP_ENCODING = Charset.forName("ISO-8859-1");

    /**
     * Utf-8 encoding
     */
    public static final Charset UTF8 = Charset.forName("utf-8");

    /**
     * Extracts Charset from Content-Type header value
     *
     * @param contentType Content-Type header value
     * @return Charset or DEFAULT_HTTP_ENCODING if it is not specified
     */
    public static Charset forContentType(String contentType) {
        return forContentType(contentType, DEFAULT_HTTP_ENCODING);
    }

    /**
     * Extracts Charset from Content-Type header value
     *
     * @param contentType    Content-Type header value
     * @param defaultCharset Will be returned if no charset found
     * @return Charset or defaultCharset
     */
    public static Charset forContentType(String contentType, Charset defaultCharset) {
        try {
            if (!StringUtils.isEmpty(contentType)) {
                String[] parts = StringUtils.split(contentType, ';');

                for (String t1 : parts) {
                    String t = t1.trim();
                    int index = t.toLowerCase().indexOf("charset=");
                    if (index != -1) {
                        String charset = t.substring(index + 8);
                        String charset1 = StringUtils.split(charset, ",;")[0];
                        return forName(charset1, defaultCharset);
                    }
                }
                return defaultCharset;
            }

            return defaultCharset;
        } catch (Exception ex) {
            log.debug(String.format("Cannot extract charset from %s", contentType), ex);
            return defaultCharset;
        }
    }

    /**
     * Safely gets charset for the specified name
     *
     * @param charsetName Charset name
     * @return Charset or null
     */
    public static Charset forName(String charsetName) {
        return forName(charsetName, null);
    }

    /**
     * Safely gets charset for the specified name
     *
     * @param charsetName    Charset name
     * @param defaultCharset Default charset (if nothing found for specified charset name)
     * @return Charset or defaultCharset
     */
    public static Charset forName(String charsetName, Charset defaultCharset) {

        try {

            return Charset.forName(charsetName);
        } catch (Exception ex) {

            try {
                Matcher matcher = CODE_PAGE_REGEX.matcher(charsetName);
                if (matcher.find()) {
                    int codePage = NumberUtils.toInteger(matcher.group(2));
                    return codePage > 0 ? Charset.forName("CP" + codePage) : defaultCharset;
                } else {
                    log.debug("Charset not found for " + charsetName, ex);
                }
            } catch (Exception e) {
                log.debug("Charset not found for " + charsetName, e);
            }


        }

        return defaultCharset;
    }
}
