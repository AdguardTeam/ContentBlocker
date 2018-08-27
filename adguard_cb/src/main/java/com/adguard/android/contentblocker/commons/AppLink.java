package com.adguard.android.contentblocker.commons;

import android.content.Context;
import android.net.Uri;

import com.adguard.android.contentblocker.BuildConfig;

public class AppLink {
    /**
     * Class for obtaining links on the filter api
     */
    public static class FilterApi {

        /**
         * Gets check filters versions url
         *
         * @return URL for checking filter version
         */
        public static String getCheckFilterVersionsUrl() {
            return BuildConfig.checkFilterVersionsUrl;
        }

        /**
         * Gets filter get url
         *
         * @return Url for getting filter rules
         */
        public static String getFilterUrl() {
            return BuildConfig.getFilterUrl;
        }
    }

    /**
     * Class for obtaining links on the adguard.com website
     */
    public static class Website {
        /**
         * Gets adguard home page url
         *
         * @return adguard home page url
         */
        public static String getHomeUrl(Context context, String from) {
            return getForwardLink(context, BuildConfig.websiteUrl, from);
        }

        /**
         * Gets adguard forum url
         *
         * @return adguard forum url
         */
        public static String getForumUrl(Context context, String from) {
            return getForwardLink(context, BuildConfig.forumUrl, from);
        }

        /**
         * Gets privacy policy url
         *
         * @return privacy policy url
         */
        public static String getPrivacyPolicyUrl(Context context, String from) {
            return getForwardLink(context, BuildConfig.privateUrl, from);
        }

        /**
         * Gets eula url
         *
         * @return eula url
         */
        public static String getEULAUrl(Context context, String from) {
            return getForwardLink(context, BuildConfig.eulaUrl, from);
        }

        /**
         * Gets url to other adguard products
         *
         * @return url to other adguard products
         */
        public static String getOtherProductUrl(Context context) {
            return getForwardLink(context, BuildConfig.otherAdguardProductUrl, "main_activity");
        }
    }

    /**
     * Class for obtaining links on the github.com website
     */
    public static class Github {
        /**
         * Gets github home page url
         *
         * @return github home page url
         */
        public static String getHomeUrl(Context context, String from) {
            return getForwardLink(context, BuildConfig.gihubUrl, from);
        }

        /**
         * Gets url to the new issue
         *
         * @return url to the new issue
         */
        public static String getNewIssueUrl(Context context, String from) {
            return getForwardLink(context, BuildConfig.gihubIssueUrl, from);
        }
    }

    /**
     * Gets forward link with application id and program version
     *
     * @param context   context
     * @param url       url
     * @param from      The name of the activity from which the method was called
     * @return Gets forward link
     */
    private static String getForwardLink(Context context, String url, String from) {
        Uri.Builder builder = Uri.parse(url).buildUpon();

        builder.appendQueryParameter("from", from);
        builder.appendQueryParameter("appid", PackageUtils.getApplicationId(context));
        builder.appendQueryParameter("v", PackageUtils.getVersionName(context));

        return builder.toString();
    }
}
