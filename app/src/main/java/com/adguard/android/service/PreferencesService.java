package com.adguard.android.service;

import com.adguard.commons.enums.FilteringQuality;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Service that stores application preferences
 */
public interface PreferencesService {

    /**
     * @return true if filters autoupdate is enabled
     */
    boolean isAutoUpdateFilters();

    /**
     * @param value true if filters autoupdate is enabled
     */
    void setAutoUpdateFilters(boolean value);

    /**
     * @return true if update over wifi only is enabled
     */
    boolean isUpdateOverWifiOnly();

    /**
     * @param value true if update over wifi only is enabled
     */
    void setUpdateOverWifiOnly(boolean value);

    /**
     * @return true if user will send anonymous security-related statistics
     */
    boolean isSendAnonymousStatistics();

    /**
     * @param value true if user will send anonymous security-related statistics
     */
    void setSendAnonymousStatistics(boolean value);

    /**
     * @return Whitelisted domains
     */
    Set<String> getWhiteList();

    /**
     * Adds whitelisted domain
     *
     * @param item Domain to add
     */
    void addToWhitelist(String item);

    /**
     * Clears whitelist
     */
    void clearWhiteList();

    /**
     * Removes whitelisted domain
     *
     * @param item Domain to remove
     */
    void removeWhiteListItem(String item);

    /**
     * @return User filter rules
     */
    Set<String> getUserRules();

    /**
     * Adds new rule to user filter
     *
     * @param item Rule to add
     */
    void addUserRuleItem(String item);

    /**
     * Removes rule from the user filter
     *
     * @param item Rule to remove
     */
    void removeUserRuleItem(String item);

    /**
     * Clears user filter
     */
    void clearUserRules();

    /**
     * Add batch of user rules.
     *
     * @param items collection of rules
     */
    void addUserRuleItems(Collection<String> items);

    /**
     * Gets last version found
     *
     * @return Last version found
     */
    String getLastVersionFound();

    /**
     * Sets last version found
     *
     * @param value Last version found
     */
    void setLastVersionFound(String value);

    /**
     * @param time Last time updates where checked
     */
    void setLastUpdateCheck(long time);

    /**
     * @return Last time updates where checked
     */
    Date getLastUpdateCheck();

    /**
     * @param code Logging level
     */
    void setLogLevel(int code);

    /**
     * @return Current logging level
     */
    int getLogLevel();

    /**
     * @param referrerString Referrer value (got from INSTALL_REFERRER receiver)
     */
    void setReferrer(String referrerString);

    /**
     * @return Referrer value (got from INSTALL_REFERRER receiver)
     */
    String getReferrer();

    /**
     * @return Current application language
     */
    String getAppLanguage();

    /**
     * @param languageCode Current application language
     */
    void setAppLanguage(String languageCode);

    /**
     * @return Current filtering quality mode.
     */
    FilteringQuality getFilteringQuality();

    /**
     * @return Device speed rank
     */
    int getDeviceSpeedRank();

    /**
     * @param quality Filtering quality mode.
     */
    void setFilteringQuality(FilteringQuality quality);

    /**
     * Gets url from which user imported his rules
     *
     * @return last imported url
     */
    String getLastImportUrl();

    /**
     * Saves url from which user imported his rules
     *
     * @param url last url from which user has imported some rules
     */
    void setLastImportUrl(String url);

    boolean isFirstStartTutorialNeeded();

    /**
     * Gets current update channel
     *
     * @return current update channel: 0 - release, 1 - beta.
     */
    int getUpdateChannel();

    /**
     * Saves new update channel
     * 0 - release, 1 - beta
     *
     * @param selectedChannel new update channel
     */
    void setUpdateChannel(int selectedChannel);

    /**
     * Whether we should show useful ads
     */
    boolean isShowUsefulAds();

    void setFilterRuleCount(int ruleCount);

    int getFilterRuleCount();
}
