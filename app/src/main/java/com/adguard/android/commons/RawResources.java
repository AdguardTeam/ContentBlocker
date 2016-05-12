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
package com.adguard.android.commons;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.util.ArrayMap;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.adguard.android.contentblocker.R;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Class for obtaining resources
 */
public class RawResources {

    /**
     * Map with languages supported by application.
     */
    public final static Map<String, Integer> SUPPORTED_LANGUAGES;
    /**
     * Default language used by application if device locale
     * is not supported.
     */
    public final static String DEFAULT_LANGUAGE = "en";
    private final static Logger LOG = LoggerFactory.getLogger(RawResources.class);

    private static Properties properties;

    private static String createTablesScript;
    private static String dropTablesScript;
    private static String insertFiltersScript;
    private static String insertFiltersLocalizationScript;
    private static String enableDefaultFiltersScript;

    static {
        SUPPORTED_LANGUAGES = new ArrayMap<>();
        SUPPORTED_LANGUAGES.put("en", R.string.app_language_english);
        SUPPORTED_LANGUAGES.put("ru", R.string.app_language_russian);
    }

    /**
     * Gets check filters versions url
     *
     * @param context Context
     * @return URL for checking filter version
     */
    public static String getCheckFilterVersionsUrl(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("check.filter.versions.url");
    }

    /**
     * Gets filter get url
     *
     * @param context Current context
     * @return Url for getting filter rules
     */
    public static String getFilterUrl(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("get.filter.url");
    }

    /**
     * Gets feedback url
     *
     * @param context Context
     * @return Feedback api url
     */
    public static String getFeedbackUrl(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("feedback.url");
    }

    /**
     * Gets application status url
     *
     * @param context Current context
     * @return Url for getting application status
     */
    public static String getApplicationStatusUrl(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("status.url");
    }

    /**
     * @param context Context
     * @return check application update info url.
     */
    public static String getCheckUpdateUrl(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("check.update.url");
    }

    /**
     * Gets purchase adguard license key url
     *
     * @param context Context
     * @return Purchase url
     */
    public static String getPurchaseAdguardLicenceKeyUrl(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("purchase.license.key.url");
    }

    /**
     * Gets adguard account url
     *
     * @param context Application context
     * @return Personal account url
     */
    public static String getAdguardAccountUrl(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("adguard.account.url");
    }

    /**
     * @param context Context
     * @return reset license url
     */
    public static String getResetLicenseUrl(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("reset.license.url");
    }

    /**
     * @param context Context
     * @return reset license url
     */
    public static String getRequestLicenseTrialUrl(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("request.trial.url");
    }

    /**
     * @param context Context
     * @return license payment url
     */
    public static String getLicensePaymentUrl(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("license.payment.url");
    }

    /**
     * @param context Current context
     * @return create tables script
     */
    public static String getCreateTablesScript(Context context) {
        if (createTablesScript == null) {
            createTablesScript = getResourceAsString(context, R.raw.create_tables);
        }

        return createTablesScript;
    }

    /**
     * @param context Current context
     * @return insert filters script string
     */
    public static String getInsertFiltersScript(Context context) {
        if (insertFiltersScript == null) {
            insertFiltersScript = getResourceAsString(context, R.raw.insert_filters);
        }

        return insertFiltersScript;
    }

    /**
     * @param context Current context
     * @return insert filters localization script string
     */
    public static String getInsertFiltersLocalizationScript(Context context) {
        if (insertFiltersLocalizationScript == null) {
            insertFiltersLocalizationScript = getResourceAsString(context, R.raw.insert_filters_localization);
        }

        return insertFiltersLocalizationScript;
    }

    /**
     * Gets update script used to update schema to a new version
     *
     * @param context    App context
     * @param oldVersion Old schema version
     * @param newVersion New schema version
     * @return Update script
     */
    public static String getUpdateScript(Context context, int oldVersion, int newVersion) {
        String updateScriptName = "update_" + oldVersion + "_" + newVersion;
        int id = context.getResources().getIdentifier(updateScriptName, "raw", context.getPackageName());

        if (id <= 0) {
            return null;
        }

        return getResourceAsString(context, id);
    }

    /**
     * @param context Current context
     * @return drop tables script
     */
    public static String getDropTablesScript(Context context) {
        if (dropTablesScript == null) {
            dropTablesScript = getResourceAsString(context, R.raw.drop_tables);
        }

        return dropTablesScript;
    }

    /**
     * @param context Current context
     * @return enable default filters script string
     */
    public static String getEnableDefaultFiltersScript(Context context) {
        if (enableDefaultFiltersScript == null) {

            List<String> languages = getInputLanguages(context);
            String defaultLanguage = cleanUpLanguageCode(Locale.getDefault().getLanguage());
            if (!languages.contains(defaultLanguage)) {
                languages.add(defaultLanguage);
            }

            enableDefaultFiltersScript = getResourceAsString(context, R.raw.enable_default_filters).replace("{0}", StringUtils.join(languages, ","));
        }

        return enableDefaultFiltersScript;
    }

    /**
     * @param context Current context
     * @return select filters script string
     */
    public static String getSelectFiltersScript(Context context) {
        return getResourceAsString(context, R.raw.select_filters).replace("{0}", Locale.getDefault().getLanguage());
    }

    /**
     * @param context Current context
     * @return application environment name
     */
    public static String getApplicationEnvironment(Context context) {
        if (properties == null) {
            if (loadProperties(context) == null) {
                return null;
            }
        }

        return properties.getProperty("application.environment");
    }

    /**
     * @param context Context
     * @return is application production environment
     */
    public static boolean isProductionEnvironment(Context context) {
        return "prod".equals(getApplicationEnvironment(context)) || isGoogleEnvironment(context);
    }

    /**
     * @param context Context
     * @return is application google environment
     */
    public static boolean isGoogleEnvironment(Context context) {
        return "google".equals(getApplicationEnvironment(context));
    }

    /**
     * @param context Context
     * @return is application amazon environment
     */
    public static boolean isAmazonEnvironment(Context context) {
        return "amazon".equals(getApplicationEnvironment(context));
    }

    /**
     * Gets resource as string
     *
     * @param context    Context
     * @param resourceId resource file id
     * @return String
     */
    private static String getResourceAsString(Context context, int resourceId) {
        try {
            return IOUtils.toString(context.getResources().openRawResource(resourceId));
        } catch (Exception ex) {
            throw new RuntimeException("Error getting resource " + resourceId, ex);
        }
    }

    private static Properties loadProperties(Context context) {
        try {
            InputStream rawResource = context.getResources().openRawResource(R.raw.application);
            properties = new Properties();
            properties.load(rawResource);

            return properties;
        } catch (Resources.NotFoundException e) {
            LOG.error("Did not find raw resource", e);
        } catch (IOException e) {
            LOG.error("Failed to open properties file");
        }

        return null;
    }

    /**
     * Gets input languages
     *
     * @param context Application context
     * @return List of input languages
     */
    private static List<String> getInputLanguages(Context context) {
        List<String> languages = new ArrayList<>();

        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            List<InputMethodInfo> ims = imm.getEnabledInputMethodList();

            for (InputMethodInfo method : ims) {
                List<InputMethodSubtype> subMethods = imm.getEnabledInputMethodSubtypeList(method, true);
                for (InputMethodSubtype subMethod : subMethods) {
                    if ("keyboard".equals(subMethod.getMode())) {
                        String currentLocale = subMethod.getLocale();
                        String language = cleanUpLanguageCode(new Locale(currentLocale).getLanguage());
                        if (!languages.contains(language)) {
                            languages.add(language);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOG.warn("Cannot get user input languages\r\n", ex);
        }

        return languages;
    }

    /**
     * Cleans up language code, leaves only first part of it
     *
     * @param language Language code (like en_US)
     * @return language (like en)
     */
    private static String cleanUpLanguageCode(String language) {
        String languageCode = StringUtils.substringBefore(language, "_");
        return StringUtils.lowerCase(languageCode);
    }
}
