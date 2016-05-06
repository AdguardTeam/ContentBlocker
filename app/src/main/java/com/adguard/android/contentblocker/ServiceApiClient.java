package com.adguard.android.contentblocker;

import android.content.Context;

import com.adguard.android.commons.RawResources;
import com.adguard.android.filtering.api.HttpServiceClient;
import com.adguard.android.model.AppConfiguration;
import com.adguard.android.model.FilterList;
import com.adguard.android.model.MobileStatusResponse;
import com.adguard.android.model.UpdateResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

    /**
     * Downloads application status info
     *
     * @param context       Application context
     * @param applicationId Application ID
     * @param versionName   Version
     * @param licenseKey    License key
     * @param purchaseToken Purchase token
     * @param storeName     Name of store like Google, Amazon, etc.
     * @param webmasterId   Webmaster ID
     * @param couponId      Coupon ID
     * @param deviceName    Device name
     * @return Response from backend
     */
    public static MobileStatusResponse downloadStatusInfo(Context context, String applicationId, String versionName,
                                                          String licenseKey, String purchaseToken, String storeName,
                                                          String webmasterId, Integer couponId, String deviceName) {
        try {
            String downloadUrl = RawResources.getApplicationStatusUrl(context)
                    .replace("{0}", UrlUtils.urlEncode(applicationId))
                    .replace("{1}", UrlUtils.urlEncode(versionName))
                    .replace("{2}", UrlUtils.urlEncode(licenseKey != null ? licenseKey : ""))
                    .replace("{3}", UrlUtils.urlEncode(Locale.getDefault().getLanguage()))
                    .replace("{4}", UrlUtils.urlEncode(purchaseToken != null ? purchaseToken : ""))
                    .replace("{5}", UrlUtils.urlEncode(storeName != null ? storeName : ""))
                    .replace("{6}", UrlUtils.urlEncode(webmasterId != null ? webmasterId : ""))
                    .replace("{7}", UrlUtils.urlEncode(couponId != null ? String.valueOf(couponId) : ""))
                    .replace("{8}", UrlUtils.urlEncode(deviceName != null ? deviceName : ""));

            final String response = downloadString(downloadUrl);
            LOG.info(response);
            return OBJECT_MAPPER.readValue(response, MobileStatusResponse.class);
        } catch (Exception ex) {
            LOG.error("Error requesting application status\r\n", ex);
        }

        return null;
    }

    /**
     * Downloads application update info
     *
     * @param context       Context
     * @param applicationId Application id
     * @param versionName   Current version name
     * @param force         true means manual update check
     */
    public static UpdateResponse downloadUpdateInfo(Context context, String applicationId, String versionName, boolean force) {
        try {
            String downloadUrl = RawResources.getCheckUpdateUrl(context)
                    .replace("{0}", UrlUtils.urlEncode(applicationId))
                    .replace("{1}", UrlUtils.urlEncode(versionName))
                    .replace("{2}", UrlUtils.urlEncode(Locale.getDefault().getLanguage()))
                    .replace("{3}", UrlUtils.urlEncode(Boolean.toString(force)))
                    .replace("{4}", "release");

            LOG.info("Send request to {}", downloadUrl);
            final String response = downloadString(downloadUrl);
            LOG.info(response);

            if (StringUtils.isEmpty(response)) {
                LOG.info("Application update response is empty");
                return null;
            }

            return OBJECT_MAPPER.readValue(response, UpdateResponse.class);
        } catch (Exception ex) {
            LOG.error("Error requesting application update\r\n", ex);
        }

        return null;
    }

    private static List<FilterList> parseFiltersVersionData(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, new TypeReference<List<FilterList>>() {
        });
    }

    /**
     * Sends feedback message to backend.
     *
     * @param context       Context
     * @param applicationId Application unique ID
     * @param versionName   Application version
     * @param userEmail     User email
     * @param feedbackType  Feedback type
     * @param message       Message content
     */
    public static String sendFeedbackMessage(Context context, String applicationId, String versionName,
                                             String userEmail, String feedbackType, String message) {
        return sendFeedbackMessage(context, applicationId, versionName, userEmail, feedbackType, message, null, null);
    }

    /**
     * Sends feedback message with additional debug info included
     *
     * @param context          Context
     * @param applicationId    Application unique ID
     * @param versionName      Application version
     * @param userEmail        User's email
     * @param feedbackType     Feedback type
     * @param message          Message text
     * @param applicationState Application configuration
     * @param debugInfo        Additional debug info
     */
    public static String sendFeedbackMessage(Context context, String applicationId, String versionName,
                                             String userEmail, String feedbackType,
                                             String message, AppConfiguration applicationState, String debugInfo) {
        String url = RawResources.getFeedbackUrl(context);
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("applicationId", applicationId);
            map.put("version", versionName);
            map.put("email", userEmail);
            map.put("language", Locale.getDefault().getLanguage());
            map.put("subject", feedbackType);
            map.put("description", message);
            map.put("applicationState", OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(applicationState));
            map.put("debugInfo", debugInfo);

            StringBuilder sb = new StringBuilder();
            for (String key : map.keySet()) {
                final String value = map.get(key);
                if (value == null) continue;

                sb.append("&");
                sb.append(key);
                sb.append("=");
                sb.append(UrlUtils.urlEncode(value));
            }

            String body = sb.toString();
            body = body.substring(1);

            LOG.info("Sending feedback. Body length is {}", body.length());
            final String response = postData(url, body);
            LOG.info("Feedback has been sent. Response is {}", response);
            return response;
        } catch (IOException ex) {
            LOG.error("Error while sending feedback message:\r\n", ex);
        }

        return null;
    }

    /**
     * Performs license reset request
     *
     * @param context       Context
     * @param applicationId Application id
     */
    public static void resetLicense(Context context, String applicationId) {
        try {
            String downloadUrl = RawResources.getResetLicenseUrl(context).replace("{0}", UrlUtils.urlEncode(applicationId));

            LOG.info("Send request to {}", downloadUrl);
            final String response = downloadString(downloadUrl);
            LOG.info(response);
        } catch (Exception ex) {
            LOG.error("Error resetting license\r\n", ex);
        }
    }

    /**
     * Performs license trial request
     *
     * @param context       Context
     * @param applicationId Application ID
     * @param webmasterId   Webmaster ID
     */
    public static MobileStatusResponse requestLicenseTrial(Context context, String applicationId, String webmasterId) {
        try {
            String downloadUrl = RawResources.getRequestLicenseTrialUrl(context);
            downloadUrl = downloadUrl.replace("{0}", UrlUtils.urlEncode(applicationId));
            downloadUrl = downloadUrl.replace("{1}", UrlUtils.urlEncode(webmasterId != null ? webmasterId : ""));

            LOG.info("Send request to {}", downloadUrl);
            final String response = downloadString(downloadUrl);
            LOG.info(response);
            return OBJECT_MAPPER.readValue(response, MobileStatusResponse.class);
        } catch (Exception ex) {
            LOG.error("Error requesting trial\r\n", ex);
        }

        return null;
    }
}
