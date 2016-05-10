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
package com.adguard.filter.http;

import org.apache.commons.lang.StringUtils;

/**
 * Contains possible HTTP methods
 */
public class HttpMethod {

    /**
     * Asks for the response identical to the one that would correspond to a GET request, but without the response body.
     * This is useful for retrieving meta-information written in response headers, without having to transport the entire content.
     */
    public static final String HEAD = "HEAD";

    /**
     * Requests a representation of the specified resource. GET should not be used for operations that cause side-effects,
     * such as using it for taking actions in web applications. One reason for this is that GET may be used arbitrarily by robots or
     * crawlers, which should not need to consider the side effects that a request should cause.
     */
    public static final String GET = "GET";

    /**
     * Submits data to be processed (e.g., from an HTML form) to the identified resource. The data is included in the body of
     * the request. This may result in the creation of a new resource or the updates of existing resources or both.
     */
    public static final String POST = "POST";

    /**
     * Uploads a representation of the specified resource.
     */
    public static final String PUT = "PUT";

    /**
     * Deletes the specified resource.
     */
    public static final String DELETE = "DELETE";

    /**
     * Echoes back the received request, so that a client can see what (if any) changes or additions have been made by intermediate servers.
     */
    public static final String TRACE = "TRACE";

    /**
     * Returns the HTTP methods that the server supports for specified URL. This can be used to check the functionality of a
     * web server by requesting '*' instead of a specific resource.
     */
    public static final String OPTIONS = "OPTIONS";

    /**
     * Converts the request connection to a transparent TCP/IP tunnel, usually to facilitate SSL-encrypted
     * communication (HTTPS) through an unencrypted HTTP proxy.
     */
    public static final String CONNECT = "CONNECT";

    /**
     * Is used to apply partial modifications to a resource.
     */
    public static final String PATCH = "PATCH";

    /**
     * Used to retrieve properties, stored as XML, from a web resource. It is also overloaded to allow one to
     * retrieve the collection structure (a.k.a. directory hierarchy) of a remote system.
     */
    public static final String PROPFIND = "PROPFIND";

    /**
     * Used to change and delete multiple properties on a resource in a single atomic act
     */
    public static final String PROPPATCH = "PROPPATCH";

    /**
     * Used to create collections (a.k.a. a directory)
     */
    public static final String MKCOL = "MKCOL";

    /**
     * Used to copy a resource from one URI to another
     */
    public static final String COPY = "COPY";

    /**
     * Used to move a resource from one URI to another
     */
    public static final String MOVE = "MOVE";

    /**
     * Used to put a lock on a resource. WebDAV supports both shared and exclusive locks.
     */
    public static final String LOCK = "LOCK";

    /**
     * Used to remove a lock from a resource
     */
    public static final String UNLOCK = "UNLOCK";

    /**
     * Checks if this type of request could contain entity body
     *
     * @param method Http method
     * @return true if there is a body
     */
    public static boolean hasEntityBody(String method) {
        return StringUtils.equals(method, POST) ||
                StringUtils.equals(method, PUT) ||
                StringUtils.equals(method, PATCH) ||
                StringUtils.equals(method, DELETE) ||
                isWebDavMethod(method);
    }

    /**
     * Checks if response to this method could contain entity body.
     * For example HEAD does not expect response with body
     *
     * @param requestMethod Request method
     * @return true if expect
     */
    public static boolean expectResponseEntityBody(String requestMethod) {
        return !StringUtils.equals(requestMethod, HEAD);
    }

    /**
     * Checks if this method is valid HTTP method
     *
     * @param method Http method
     * @return true if method is valid
     */
    public static boolean isValidMethod(String method) {
        return StringUtils.equals(method, POST) ||
                StringUtils.equals(method, HEAD) ||
                StringUtils.equals(method, GET) ||
                StringUtils.equals(method, PUT) ||
                StringUtils.equals(method, DELETE) ||
                StringUtils.equals(method, TRACE) ||
                StringUtils.equals(method, OPTIONS) ||
                StringUtils.equals(method, CONNECT) ||
                StringUtils.equals(method, PATCH);
    }

    /**
     * Checks if this method is WebDAV method.
     *
     * @param method Http method to check
     * @return true if this is WebDAV method.
     */
    public static boolean isWebDavMethod(String method) {
        return StringUtils.equals(method, PROPFIND) ||
                StringUtils.equals(method, PROPPATCH) ||
                StringUtils.equals(method, MKCOL) ||
                StringUtils.equals(method, COPY) ||
                StringUtils.equals(method, MOVE) ||
                StringUtils.equals(method, LOCK) ||
                StringUtils.equals(method, UNLOCK);
    }
}
