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

import com.adguard.filter.html.HtmlElements;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Content type
 */
public enum ContentType {

    /**
     * No content type
     */
    NONE(0),

    /**
     * All except listed below
     */
    OTHER(1),

    /**
     * Image (png/jpg/jpeg etc)
     */
    IMAGE(2),

    /**
     * Javascript (or any other script)
     */
    SCRIPT(4),

    /**
     * Requests for CSS
     */
    STYLE(8),

    /**
     * Object (swf/flash/plugins)
     */
    OBJECT(16),

    /**
     * Ajax requests. We use X-Requested-With header to check if request is Ajax.
     * Although remember, that this header is not always set (only if jquery is used).
     */
    XML_HTTP_REQUEST(32),

    /**
     * Media files (mp3, mp4, avi and such)
     */
    MEDIA(64),

    /**
     * Fonts (detected by file extension)
     */
    FONT(128),

    /**
     * Html document
     */
    DOCUMENT(256),

    /**
     * Requests sent from a plugin objects (flash, etc)
     */
    OBJECT_SUBREQUEST(512),

    /**
     * Any content type
     */
    ANY(OTHER.flagValue | IMAGE.flagValue | SCRIPT.flagValue | STYLE.flagValue |
            OBJECT.flagValue | XML_HTTP_REQUEST.flagValue | MEDIA.flagValue | FONT.flagValue |
            DOCUMENT.flagValue | OBJECT_SUBREQUEST.flagValue);

    private static final Map<String, ContentType> FILE_EXTENSION_CONTENT_TYPE = new HashMap<>();
    private static final Map<String, ContentType> HTML_ELEMENTS_CONTENT_TYPE = new HashMap<>();

    static {
        FILE_EXTENSION_CONTENT_TYPE.put(".js", SCRIPT);
        FILE_EXTENSION_CONTENT_TYPE.put(".json", SCRIPT);
        FILE_EXTENSION_CONTENT_TYPE.put(".vbs", SCRIPT);
        FILE_EXTENSION_CONTENT_TYPE.put(".coffee", SCRIPT);

        FILE_EXTENSION_CONTENT_TYPE.put(".jpg", IMAGE);
        FILE_EXTENSION_CONTENT_TYPE.put(".jpeg", IMAGE);
        FILE_EXTENSION_CONTENT_TYPE.put(".gif", IMAGE);
        FILE_EXTENSION_CONTENT_TYPE.put(".png", IMAGE);
        FILE_EXTENSION_CONTENT_TYPE.put(".tiff", IMAGE);
        FILE_EXTENSION_CONTENT_TYPE.put(".psd", IMAGE);
        FILE_EXTENSION_CONTENT_TYPE.put(".ico", IMAGE);

        FILE_EXTENSION_CONTENT_TYPE.put(".css", STYLE);
        FILE_EXTENSION_CONTENT_TYPE.put(".less", STYLE);

        FILE_EXTENSION_CONTENT_TYPE.put(".jar", OBJECT);
        FILE_EXTENSION_CONTENT_TYPE.put(".swf", OBJECT);

        FILE_EXTENSION_CONTENT_TYPE.put(".wav", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".mp3", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".mp4", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".avi", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".flv", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".m3u", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".webm", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".mpeg", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".3gp", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".3g2", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".3gpp", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".3gpp2", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".ogg", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".mov", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".qt", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".vbm", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".mkv", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".webm", MEDIA);
        FILE_EXTENSION_CONTENT_TYPE.put(".gifv", MEDIA);

        FILE_EXTENSION_CONTENT_TYPE.put(".ttf", FONT);
        FILE_EXTENSION_CONTENT_TYPE.put(".otf", FONT);
        FILE_EXTENSION_CONTENT_TYPE.put(".woff", FONT);
        FILE_EXTENSION_CONTENT_TYPE.put(".woff2", FONT);
        FILE_EXTENSION_CONTENT_TYPE.put(".eot", FONT);

        HTML_ELEMENTS_CONTENT_TYPE.put(HtmlElements.SCRIPT, SCRIPT);
        HTML_ELEMENTS_CONTENT_TYPE.put(HtmlElements.LINK, STYLE);
        HTML_ELEMENTS_CONTENT_TYPE.put(HtmlElements.IMG, IMAGE);
        HTML_ELEMENTS_CONTENT_TYPE.put(HtmlElements.OBJECT, OBJECT);
        HTML_ELEMENTS_CONTENT_TYPE.put(HtmlElements.EMBED, OBJECT);
        HTML_ELEMENTS_CONTENT_TYPE.put(HtmlElements.IFRAME, DOCUMENT);
    }

    /**
     * Detects content type using request url
     *
     * @param url Url
     * @return Content type detected
     */
    public static ContentType detectContentType(URL url) {
        String path = url.getPath();

        for (Map.Entry<String, ContentType> entry : FILE_EXTENSION_CONTENT_TYPE.entrySet()) {
            if (StringUtils.endsWith(path, entry.getKey())) {
                return entry.getValue();
            }
        }

        return ContentType.OTHER;
    }

    /**
     * Detects content type using request url
     *
     * @param url Url
     * @return Content type detected
     */
    public static ContentType detectContentType(String url) {
        try {
            URL requestUrl = new URL(url);
            return detectContentType(requestUrl);
        } catch (Exception ex) {
            LoggerFactory.getLogger(ContentType.class).debug(String.format("Error detecting content type for %s", url), ex);
        }

        return ContentType.OTHER;
    }

    /**
     * Translates a set of ContentType enums into a numeric mask
     *
     * @param flags Set of ContentType enums
     * @return numeric representation of the set
     */
    public static int getMask(Set<ContentType> flags) {
        int mask = 0;

        for (ContentType contentType : flags) {
            mask |= contentType.getFlagValue();
        }

        return mask;
    }

    private final int flagValue;

    ContentType(int flagValue) {
        this.flagValue = flagValue;
    }

    /**
     * @return Flag value (used to create mask)
     */
    public int getFlagValue() {
        return flagValue;
    }

    /**
     * Checks if response content type has one of the specified prefixes
     *
     * @param responseContentType Response content type
     * @param contentTypes        Content types to check
     * @return true if any of the prefixes match
     */
    private static boolean isContentType(String responseContentType, String... contentTypes) {

        for (String contentType : contentTypes) {
            if (StringUtils.startsWith(responseContentType, contentType)) {
                return true;
            }
        }

        return false;
    }
}
