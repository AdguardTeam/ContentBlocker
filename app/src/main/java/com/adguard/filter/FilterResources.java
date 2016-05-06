package com.adguard.filter;

import android.content.Context;
import android.content.res.AssetManager;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

/**
 * Class for obtaining resources
 */
public class FilterResources {

    private static String safebrowsingBlockedPageHtml;

    /**
     * @return Html for the page blocked by SafebrowsingFilter
     */
    public static String getSafebrowsingBlockedPageHtml(Context context) {
        if (safebrowsingBlockedPageHtml == null) {
            safebrowsingBlockedPageHtml = getResourceAsString(context, "safebrowsingBlockedPage.html");
        }

        return safebrowsingBlockedPageHtml;
    }

    /**
     * Gets resource as string
     *
     * @param resourcePath Path to resource file
     * @return String
     */
    private static String getResourceAsString(Context context, String resourcePath) {
        try {
            InputStream stream = FilterResources.class.getResourceAsStream(resourcePath);
            if (stream != null) {
                return IOUtils.toString(stream);
            } else {
                AssetManager assets = context.getAssets();
                InputStream inputStream = assets.open(resourcePath);
                String string = IOUtils.toString(inputStream);
                inputStream.close();
                return string;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error getting resource " + resourcePath, ex);
        }
    }

    /**
     * Gets resource as byte array
     *
     * @param resourcePath Path to resource file
     * @return String
     */
    private static byte[] getResourceAsByteArray(Context context, String resourcePath) {
        try {
            InputStream stream = FilterResources.class.getResourceAsStream(resourcePath);
            if (stream != null) {
                return IOUtils.toByteArray(stream);
            } else {
                AssetManager assets = context.getAssets();
                InputStream inputStream = assets.open(resourcePath);
                byte[] buf = IOUtils.toByteArray(inputStream);
                inputStream.close();
                return buf;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error getting resource " + resourcePath, ex);
        }
    }
}
