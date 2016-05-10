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
package com.adguard.commons.web;

import com.adguard.commons.utils.ReservedDomains;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

/**
 * Helper for working with URLs (extract keywords, extract domain name)
 */
public class UrlUtils {

    public static final String LOCALHOST_ADDRESS = "127.0.0.1";
    private final static Logger LOG = LoggerFactory.getLogger(UrlUtils.class);
    public final static int DEFAULT_READ_TIMEOUT = 10000; // 10 seconds
    public final static int DEFAULT_SOCKET_TIMEOUT = 10000; // 10 seconds
    private final static List<KeywordMatcher> SEARCH_ENGINES;

    static {
        SEARCH_ENGINES = new ArrayList<>();
        SEARCH_ENGINES.add(new KeywordMatcher("^http://(www\\.)?google.*", new String[]{"q"}));
        SEARCH_ENGINES.add(new KeywordMatcher("^http://(www\\.?)bing.*", new String[]{"q"}));
        SEARCH_ENGINES.add(new KeywordMatcher("^http://([a-z]*\\.)?yahoo.com.*", new String[]{"p"}));
        SEARCH_ENGINES.add(new KeywordMatcher("^http://nova\\.rambler\\.ru/search.*", new String[]{"query"}));
        SEARCH_ENGINES.add(new KeywordMatcher("^http://yandex\\.ru/yandsearch.*", new String[]{"text"}));
    }

    /**
     * Tries to decode specified text (also trying to autodetect encoding).
     * If something gone wrong -- returns source text.
     *
     * @param text Text to decode
     * @return Decoded string
     */
    public static String urlDecode(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        try {
            if (text.contains("%8")) {
                return URLDecoder.decode(text, "UTF-8");
            }

            return URLDecoder.decode(text, "ascii");
        } catch (Exception ex) {
            LOG.warn("Error decoding " + text, ex);
        }

        return text;
    }

    /**
     * Tries to url encode specified text (using utf-8 encoding).
     * If something gone wrong -- returns input text as is.
     *
     * @param text Text to encode
     * @return Encoded string
     */
    public static String urlEncode(String text) {
        try {
            return URLEncoder.encode(text, "utf-8");
        } catch (Exception ex) {
            LOG.warn("Error encoding " + text, ex);
        }
        return text;
    }


    /**
     * Removes jsessionid from string
     *
     * @return url without jsessionid
     */
    public static String removeJSessionId(String str) {
        // Removing jsessionid
        if (!StringUtils.isEmpty(str) && StringUtils.contains(str.toLowerCase(), ";jsessionid")) {
            return str.substring(0, StringUtils.indexOf(str, ";jsessionid"));
        }

        return str;
    }

    /**
     * Gets parameter value from the url.
     * If tryRewrited is true - tries to parse rewrited url using getRewritedParameter method
     *
     * @param url           url
     * @param parameterName parameter name
     * @param tryRewrited   try to extract from rewrited url
     * @return parameter value
     */
    public static String getParameter(String url, String parameterName, boolean tryRewrited) {
        String parameterValue = getParameter(url, parameterName);

        if (StringUtils.isEmpty(parameterValue) && tryRewrited) {
            parameterValue = getRewritedParameter(url, parameterName);
        }

        return parameterValue;
    }

    /**
     * Gets parameter value from the url
     *
     * @param url           url
     * @param parameterName parameter name
     * @return parameter value
     */
    public static String getParameter(String url, String parameterName) {
        Map<String, String> parameters = getParameters(url);
        return parameters == null ? null : parameters.get(parameterName);
    }

    /**
     * For rewrited urls like 'http://dev.com/q/search%20terms/anotherparameter/anotherparametervalue'
     *
     * @param url           url
     * @param parameterName parameter name
     * @return rewrited parameter
     */
    public static String getRewritedParameter(String url, String parameterName) {

        String[] parts = StringUtils.splitPreserveAllTokens(url, '/');

        if (parts == null || parts.length < 2) {
            return null;
        }

        String parameterValue = null;

        // Searching for the specified parameter name in url
        for (int i = 0; i < parts.length; i++) {
            if (parameterName.equals(parts[i])) {
                if (i < (parts.length - 1)) {
                    // parameter name found, breaking loop
                    parameterValue = parts[i + 1];
                    break;
                }
            }
        }

        // Removing jsessionid
        parameterValue = removeJSessionId(parameterValue);

        return parameterValue;
    }

    /**
     * Checks if string contains ASCII symbols only
     *
     * @param input String
     * @return true if the input string is ASCII
     */
    public static boolean isASCII(String input) {

        if (input == null) {
            return true;
        }

        for (int i = 0; i < input.length(); i++) {
            int c = input.charAt(i);
            if (c > 0x7F) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts domain to punycode if needed.
     * For ascii domains -- does nothing.
     *
     * @param domainName Domain name
     * @return Punycode domain name
     */
    public static String toPunycode(String domainName) {

        try {
            return IDN.toASCII(domainName);
        } catch (Exception ex) {
            LOG.debug("Cannot convert " + domainName + " to punycode", ex);
        }

        return domainName;
    }

    /**
     * Checks if specified request url is third-party or not
     *
     * @param requestUrl Request url
     * @param referrer   Referrer url
     * @return true if request is third party
     */
    public static boolean isThirdPartyRequest(String requestUrl, String referrer) {
        String domainName = getSecondLevelDomainName(requestUrl);
        String refDomainName = getSecondLevelDomainName(referrer);
        return referrer != null && !StringUtils.equals(domainName, refDomainName);
    }

    /**
     * Extracts port from URI
     *
     * @param url URL to get port from
     * @return port value.
     */
    public static int getPort(URL url) {
        int port = url.getPort();

        if (port >= 0) {
            return port;
        } else if ("http".equals(url.getProtocol())) {
            return 80;
        } else if ("https".equals(url.getProtocol())) {
            return 443;
        } else if ("ftp".equals(url.getProtocol())) {
            return 21;
        }

        return port;
    }

    /**
     * Extracts host name from the url
     *
     * @param url Url to extract from
     * @return Host name
     */
    public static String getHost(String url) {
        try {
            if (StringUtils.isEmpty(url)) {
                return null;
            }

            int firstIdx = url.indexOf("//");
            if (firstIdx == -1) {
                return null;
            }
            int nextSlashIdx = url.indexOf("/", firstIdx + 2);
            int startParamsIdx = url.indexOf("?", firstIdx + 2);
            int colonIdx = url.indexOf(":", firstIdx + 2);

            int lastIdx = nextSlashIdx;
            if (startParamsIdx > 0 && (startParamsIdx < nextSlashIdx || lastIdx == -1)) {
                lastIdx = startParamsIdx;
            }

            if (colonIdx > 0 && (colonIdx < lastIdx || lastIdx == -1)) {
                lastIdx = colonIdx;
            }

            return lastIdx == -1 ? url.substring(firstIdx + 2) : url.substring(firstIdx + 2, lastIdx);
        } catch (Exception ex) {
            LOG.debug("Cannot extract host from " + url, ex);
            return null;
        }
    }

    /**
     * Extracts relative uri from raw request uri
     *
     * @param rawUri Raw request uri
     * @return Relative uri
     */
    public static String getRelativeUri(String rawUri) {
        if (StringUtils.isEmpty(rawUri) || rawUri.startsWith("/")) {
            // rawUri is already relative
            return rawUri;
        }
        final String protocolPrefixEnd = "//";
        int protocolEndIndex = rawUri.indexOf(protocolPrefixEnd) + protocolPrefixEnd.length();
        int pointIndex = rawUri.indexOf('.');
        if (protocolEndIndex == -1 || protocolEndIndex > pointIndex) {
            return rawUri;
        }
        int slashIndex = rawUri.indexOf('/', pointIndex);
        return slashIndex == -1 ? rawUri : rawUri.substring(slashIndex);
    }

    /**
     * Extracts domain name from the url. Also crops www.
     *
     * @param url url
     * @return domain name
     */
    public static String getDomainName(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }

        try {
            if (!StringUtils.startsWith(url, "http://") &&
                    !StringUtils.startsWith(url, "https://")) {
                url = "http://" + url;
            }
            return getDomainName(new URL(url));
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    /**
     * Checks if specified parameter is valid domain name
     *
     * @param domainName Domain name
     * @return true if it is really a domain name
     */
    @SuppressWarnings("UnusedDeclaration")
    public static boolean isDomainName(String domainName) {

        if (StringUtils.isEmpty(domainName) ||
                !StringUtils.contains(domainName, ".")) {
            return false;
        }

        String url = "http://" + domainName;
        String normalizedDomainName = getDomainName(url);

        //noinspection RedundantIfStatement
        if (normalizedDomainName != null && "".equals(normalizedDomainName.replaceAll("[a-zA-Z0-9-.]+", ""))) {
            return true;
        }
        return false;
    }

    /**
     * Gets all possible domain names up to 2nd level.
     * <p/>
     * For example, for "test.domain.name.com"
     * this method will return:<br/>
     * name.com
     * domain.name.com
     * test.domain.name.com
     *
     * @param domainName Domain name to extract
     * @return List of possible domain names
     */
    public static List<String> getTopDomainNames(String domainName) {

        String[] parts = StringUtils.split(domainName, ".");

        if (parts == null || parts.length <= 1) {
            return Arrays.asList(domainName);
        }

        List<String> domainNames = new ArrayList<>();

        for (int i = 0; i < parts.length - 1; i++) {

            StringBuilder sb = new StringBuilder();

            for (int j = 0; j + i < parts.length; j++) {
                sb.append(parts[j + i]);
                sb.append(".");
            }

            sb.deleteCharAt(sb.length() - 1);
            domainNames.add(sb.toString());
        }

        return domainNames;
    }

    /**
     * Extracts domain name from the url. Also crops www.
     *
     * @param url url
     * @return domain name
     */
    public static String getDomainName(URL url) {
        String host = url.getHost();

        if (host == null) {
            return null;
        }

        int dotIndex = host.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex == (host.length() - 1)) {
            // Check that dot is not the first or last char
            return null;
        }

        return StringUtils.lowerCase(cropWww(host));
    }

    /**
     * Crops www. prefix from the domain name
     *
     * @param domainName domain
     * @return domain without www
     */
    public static String cropWww(String domainName) {
        if (StringUtils.isEmpty(domainName)) {
            return null;
        }
        if (domainName.startsWith("www.")) {
            return domainName.substring(4);
        }

        return domainName;
    }

    /**
     * Adds parameter to a query string
     *
     * @param url            url
     * @param parameterName  parameter name
     * @param parameterValue parameter value
     */
    @SuppressWarnings("UnusedDeclaration")
    public static String addParameter(String url, String parameterName, String parameterValue) {
        try {
            if (url == null) {
                return null;
            }

            StringBuilder targetUrl = new StringBuilder();
            targetUrl.append(url);

            URL testURL = new URL(url);
            if (testURL.getQuery() != null) {
                targetUrl.append("&");
            } else if ("".equals(testURL.getPath())) {
                targetUrl.append("/?");
            } else {
                targetUrl.append("?");
            }

            targetUrl.append(parameterName);
            targetUrl.append("=");
            targetUrl.append(parameterValue);

            return targetUrl.toString();
        } catch (MalformedURLException ex) {
            return url;
        }
    }

    /**
     * Parses parameters from the query string
     *
     * @param queryString Query string or post data
     * @return Parameters map
     */
    public static Map<String, String> parseQueryString(String queryString, String charset) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(queryString)) {
            return new HashMap<>();
        }

        String[] params = queryString.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String[] pair = param.split("=");

            if (pair.length == 2) {
                String name = pair[0];
                String value = charset == null ? pair[1] : URLDecoder.decode(pair[1], charset);
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * Extracts path and query from the url
     *
     * @param url Url
     * @return path and query
     */
    public static String getPathAndQuery(URL url) {

        String path = url.getPath();
        String query = url.getQuery();

        if (StringUtils.isNotEmpty(query)) {
            return path + "?" + query;
        }

        return path;
    }

    /**
     * Gets parameters from query string
     *
     * @param url url
     * @return GET parameters map
     */
    public static Map<String, String> getParameters(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }

        int index = url.indexOf("?");

        if (index == -1) {
            return null;
        }

        String query = url.substring(index + 1);
        Map<String, String> map = null;
        try {
            map = parseQueryString(query, null);
        } catch (Exception ex) {
            LOG.error("Error parsing query string " + url, ex);
        }
        return map;
    }

    /**
     * Downloads string from the specified url
     *
     * @param url Url
     * @return Response
     */
    @SuppressWarnings("UnusedDeclaration")
    public static String downloadString(String url) throws MalformedURLException {
        return downloadString(new URL(url));
    }

    /**
     * Downloads string from the specified url
     *
     * @param url               Url
     * @param readTimeout       Read timeout
     * @param connectionTimeout Connection timeout
     * @return Downloaded string or null
     * @throws MalformedURLException
     */
    public static String downloadString(String url, int readTimeout, int connectionTimeout) throws MalformedURLException {
        return downloadString(new URL(url), null, readTimeout, connectionTimeout, "utf-8");
    }

    /**
     * Tries to download from the specified url for triesCount times
     *
     * @param url        Url
     * @param triesCount Tries count
     * @return Repsponse
     */
    public static String downloadString(String url, int triesCount) throws MalformedURLException {
        return downloadString(new URL(url), null, DEFAULT_READ_TIMEOUT, DEFAULT_SOCKET_TIMEOUT, "utf-8", triesCount);
    }

    /**
     * Downloads content from the specified url.
     * Returns null if there's an error.
     *
     * @param url url
     * @return downloaded string
     */
    public static String downloadString(URL url) {
        return downloadString(url, null, DEFAULT_READ_TIMEOUT, DEFAULT_SOCKET_TIMEOUT, "utf-8");
    }

    /**
     * Downloads content from the specified url using specified proxy (or do not using it).
     * Returns null if there's an error.
     *
     * @param url   url
     * @param proxy proxy to use
     */
    @SuppressWarnings("UnusedDeclaration")
    public static String downloadString(URL url, Proxy proxy) {
        return downloadString(url, proxy, DEFAULT_READ_TIMEOUT, DEFAULT_SOCKET_TIMEOUT, "utf-8");
    }

    /**
     * Downloads content from the specified url using specified proxy (or do not using it) and timeouts.
     * Returns null if there's an error.
     *
     * @param url           url
     * @param proxy         proxy to use
     * @param readTimeout   read timeout
     * @param socketTimeout connection timeout
     * @return Downloaded string
     */
    public static String downloadString(URL url, Proxy proxy, int readTimeout, int socketTimeout, String encoding) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.85 Safari/537.36");
            connection.setReadTimeout(readTimeout);
            connection.setConnectTimeout(socketTimeout);
            connection.connect();
            if (connection.getResponseCode() >= 400) {
                throw new IOException("Response status is " + connection.getResponseCode());
            }

            if (connection.getResponseCode() >= 301) {
                String location = connection.getHeaderField("Location");
                // HttpURLConnection does not follow redirects from HTTP to HTTPS
                // So we handle it manually
                return downloadString(new URL(location), proxy, readTimeout, socketTimeout, encoding);
            }

            if (connection.getResponseCode() == 204) {
                return StringUtils.EMPTY;
            }

            inputStream = connection.getInputStream();
            return IOUtils.toString(inputStream, encoding);
        } catch (IOException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("Error downloading string from {}:\r\n", url, ex);
            } else {
                LOG.warn("Cannot download string from {}: {}", url, ex.getMessage());
            }
            // Ignoring exception
            return null;
        } finally {
            IOUtils.closeQuietly(inputStream);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Downloads content from the specified url using specified proxy (or do not using it) and timeouts.
     * Returns null if there's an error.
     *
     * @param url           url
     * @param proxy         proxy to use
     * @param readTimeout   read timeout
     * @param socketTimeout connection timeout
     * @return Downloaded string
     */
    public static String downloadString(URL url, Proxy proxy, int readTimeout, int socketTimeout, String encoding, int triesCount) {
        IOException lastException = null;

        for (int i = 0; i < triesCount; i++) {
            lastException = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;

            try {
                connection = (HttpURLConnection) (proxy == null ? url.openConnection() : url.openConnection(proxy));
                connection.setReadTimeout(readTimeout);
                connection.setConnectTimeout(socketTimeout);
                connection.connect();
                inputStream = connection.getInputStream();
                return IOUtils.toString(inputStream, encoding);
            } catch (IOException ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Error downloading string from {}. Try number: {}. Cause:\r\n", url, triesCount, ex);
                } else {
                    LOG.warn("Error downloading string from {}. Try number: {}. Cause:{}", url, triesCount, ex.getMessage());
                }
                lastException = ex;
            } finally {
                IOUtils.closeQuietly(inputStream);
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        if (lastException != null) {
            LOG.error("Could not download string from url: " + url, lastException);
        }

        return null;
    }


    /**
     * Extracts keywords from url if possible.
     *
     * @param url url
     * @return keywords
     */
    public static String extractKeywords(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }

        for (KeywordMatcher matcher : SEARCH_ENGINES) {
            String keywords = matcher.matchKeywords(url);

            if (keywords != null) {
                return keywords.replace("+", " ");
            }
        }

        return null;
    }

    /**
     * Sends a post request
     *
     * @param url           url
     * @param postData      data to post
     * @param encoding      response encoding
     * @param contentType   content type
     * @param readTimeout   socket read timeout
     * @param socketTimeout socket connect timeout
     * @return downloaded string
     */
    public static String postRequest(String url, String postData, String encoding, String contentType, int readTimeout, int socketTimeout) {

        try {
            return postRequest(new URL(url), postData, encoding, contentType, readTimeout, socketTimeout);
        } catch (MalformedURLException ex) {
            LOG.error("Error posting request to {}, post data length={}", url, StringUtils.length(postData), ex);
            return null;
        }
    }

    /**
     * Sends a post request
     *
     * @param url         url
     * @param postData    data to post
     * @param encoding    response encoding
     * @param contentType content type
     * @return downloaded string
     */
    public static String postRequest(URL url, String postData, String encoding, String contentType) {
        return postRequest(url, postData, encoding, contentType, DEFAULT_READ_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * Sends a POST request
     *
     * @param url           URL
     * @param postData      Post body
     * @param encoding      Post body encoding
     * @param contentType   Post body content type
     * @param readTimeout   Read timeout
     * @param socketTimeout Socket timeout
     * @return Response
     */
    public static String postRequest(URL url, String postData, String encoding, String contentType, int readTimeout, int socketTimeout) {
        return postRequest(url, postData, encoding, contentType, false, readTimeout, socketTimeout);
    }

    /**
     * Sends a POST request
     *
     * @param url           URL
     * @param postData      Post request body
     * @param encoding      Post request body encoding
     * @param contentType   Body content type
     * @param compress      If true - compress bod
     * @param readTimeout   Read timeout
     * @param socketTimeout Socket timeout
     * @return Response
     */
    public static String postRequest(URL url, String postData, String encoding, String contentType, boolean compress, int readTimeout, int socketTimeout) {
        HttpURLConnection connection = null;
        OutputStream outputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            if (contentType != null) {
                connection.setRequestProperty("Content-Type", contentType);
            }
            if (compress) {
                connection.setRequestProperty("Content-Encoding", "gzip");
            }
            connection.setConnectTimeout(socketTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setDoOutput(true);
            connection.connect();
            if (postData != null) {
                outputStream = connection.getOutputStream();

                if (compress) {
                    outputStream = new GZIPOutputStream(outputStream);
                }

                if (encoding != null) {
                    IOUtils.write(postData, outputStream, encoding);
                } else {
                    IOUtils.write(postData, outputStream);
                }

                if (compress) {
                    ((GZIPOutputStream) outputStream).finish();
                } else {
                    outputStream.flush();
                }
            }

            return IOUtils.toString(connection.getInputStream(), encoding);
        } catch (Exception ex) {
            LOG.error("Error posting request to {}, post data length={}\r\n", url, StringUtils.length(postData), ex);
            // Ignoring exception
            return null;
        } finally {
            IOUtils.closeQuietly(outputStream);

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Gets 2nd level domain name from the specified url
     *
     * @param url Url to check
     * @return 2nd level domain name
     */
    public static String getSecondLevelDomainName(String url) {
        String host = (url != null && url.contains("//")) ? getHost(url) : url;

        if (StringUtils.isEmpty(host)) {
            return null;
        }

        String[] parts = StringUtils.split(host, '.');

        if (parts.length <= 2) {
            return host;
        }

        boolean containsTwoLvlPostfix;
        String twoPartDomain = parts[parts.length - 2] + "." + parts[parts.length - 1];

        containsTwoLvlPostfix = ReservedDomains.isReservedDomainName(twoPartDomain);

        String threePartDomain = parts[parts.length - 3] + "." + twoPartDomain;
        if (parts.length == 3 && containsTwoLvlPostfix) {
            return threePartDomain;
        }
        if (ReservedDomains.isReservedDomainName(threePartDomain)) {
            if (parts.length == 3) {
                return threePartDomain;
            }

            return parts[parts.length - 4] + "." + threePartDomain;
        }

        return containsTwoLvlPostfix ? threePartDomain : twoPartDomain;
    }

    /**
     * Checks if specified domain name is in the list of the domainNames
     * or if it is sub-domain of any domain in the list.
     *
     * @param domainName  Domain name to check
     * @param domainNames List of domain names
     * @return true if domain (or it's top-level domain) is in the list
     */
    public static boolean isDomainOrSubDomain(String domainName, List<String> domainNames) {
        if (CollectionUtils.isEmpty(domainNames)) {
            return false;
        }

        for (String domainToCheck : domainNames) {
            if (domainName.equals(domainToCheck) ||
                    // Optimizing end checking: http://jira.performix.ru/browse/AG-6587?focusedCommentId=23526&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-23526
                    (domainName.endsWith(domainToCheck) && domainName.endsWith("." + domainToCheck))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Class for matching keywords
     */
    private static class KeywordMatcher {

        private final Pattern urlPattern;
        private final String[] parameters;

        private KeywordMatcher(String urlPattern, String[] parameters) {
            this.urlPattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
            this.parameters = parameters;
        }

        public String matchKeywords(String url) {
            if (url == null || !urlPattern.matcher(url).matches()) {
                return null;
            }

            Map<String, String> queryParameters = getParameters(url);

            if (queryParameters == null) {
                return null;
            }

            for (String param : parameters) {
                String keywords = queryParameters.get(param);

                if (keywords != null) {
                    return keywords;
                }
            }

            return null;
        }
    }

}