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
package com.adguard.android.contentblocker;

import android.content.Context;

import com.adguard.android.commons.RawResources;
import com.adguard.android.filtering.api.HttpServiceClient;
import com.adguard.android.model.FilterList;
import com.adguard.commons.web.UrlUtils;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Special client to communicate with out backend
 */
public class ServiceApiClient extends HttpServiceClient {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceApiClient.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        OBJECT_MAPPER.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        OBJECT_MAPPER.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    /**
     * Downloads filter rules
     *
     * @param filterId    Filter id
     * @param webmasterId Webmaster ID
     * @return List of rules
     * @throws IOException
     */
    public static List<String> downloadFilterRules(Context context, int filterId, String webmasterId) throws IOException {
        String downloadUrl = RawResources.getFilterUrl(context);
        downloadUrl = downloadUrl.replace("{0}", UrlUtils.urlEncode(Integer.toString(filterId)));
        downloadUrl = downloadUrl.replace("{1}", UrlUtils.urlEncode(webmasterId != null ? webmasterId : ""));
        String response = downloadString(downloadUrl);

        String[] rules = StringUtils.split(response, "\r\n");
        List<String> filterRules = new ArrayList<>();
        for (String line : rules) {
            String rule = StringUtils.trim(line);
            if (!StringUtils.isEmpty(rule)) {
                filterRules.add(rule);
            }
        }

        return filterRules;
    }

    /**
     * Downloads filter versions.
     *
     * @param filters list
     * @return filters list with downloaded versions
     */
    public static List<FilterList> downloadFilterVersions(Context context, List<FilterList> filters) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        for (FilterList filter : filters) {
            sb.append(filter.getFilterId());
            sb.append(",");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        } else {
            LOG.info("Empty filters list, exiting");
            return null;
        }

        String downloadUrl = RawResources.getCheckFilterVersionsUrl(context).replace("{0}", UrlUtils.urlEncode(sb.toString()));
        String response = downloadString(downloadUrl);
        if (response == null) {
            return null;
        }

        LOG.debug("Filters update check response: {}", response);

        return parseFiltersVersionData(response);
    }

    private static List<FilterList> parseFiltersVersionData(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, new TypeReference<List<FilterList>>() {
        });
    }
}
