/*
 * This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 * Copyright © 2019 AdGuard Content Blocker. All rights reserved.
 * <p/>
 * AdGuard Content Blocker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * <p/>
 * AdGuard Content Blocker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
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
        public static String getOtherProductUrl(Context context, String from) {
            return getForwardLink(context, BuildConfig.otherAdguardProductUrl, from);
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
            return getForwardLink(context, BuildConfig.githubUrl, from);
        }

        /**
         * Gets url to the new issue
         *
         * @return url to the new issue
         */
        public static String getNewIssueUrl(Context context, String from) {
            return getForwardLink(context, BuildConfig.githubIssueUrl, from);
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
