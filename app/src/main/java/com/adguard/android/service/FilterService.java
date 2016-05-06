package com.adguard.android.service;

import android.app.Activity;
import com.adguard.android.model.FilterList;

import java.util.List;
import java.util.Set;

/**
 * Service that manages ad filters
 */
public interface FilterService {

    void checkFiltersUpdates(Activity activity);

    /**
     * @return List of filters
     */
    List<FilterList> getFilters();

    /**
     * @return Filter list count
     */
    int getFilterListCount();

    int getEnabledFilterListCount();

    int getFilterRuleCount();

    /**
     * Checks filters updates and returns filters that were updated
     *
     * @return List of filters that were updated
     */
    List<FilterList> checkFilterUpdates();

    /**
     * Schedules filters updates periodic job.
     */
    void scheduleFiltersUpdate();

    /**
     * Updates filter status.
     *
     * @param filter  Filter status
     * @param enabled true if filter should be enabled
     */
    void updateFilterEnabled(FilterList filter, boolean enabled);

    /**
     * @return Whitelist
     */
    Set<String> getWhiteList();

    /**
     * @param item Adds item to whitelist
     */
    void addToWhitelist(String item);

    /**
     * Clears whitelist
     */
    void clearWhiteList();

    /**
     * Removes item from the whitelist
     *
     * @param item Item to remove
     */
    void removeWhiteListItem(String item);

    /**
     * @return User filter rules
     */
    Set<String> getUserRules();

    /**
     * Adds rule to the user filter
     *
     * @param item Item to add
     */
    void addUserRuleItem(String item);

    /**
     * Removes rule from the user filter
     *
     * @param item Item to remove
     */
    void removeUserRuleItem(String item);

    /**
     * Clears user filter
     */
    void clearUserRules();

	/**
	 * Downloads and adds batch of user rules from specified url.
	 *
	 * @param url url
	 */
    void importUserRulesFromUrl(Activity activity, String url);

    /**
     * @return list of all enabled rules
     */
    List<String> getAllEnabledRules(boolean useCosmetics);

    /**
     * Is show useful ads filter enabled
     *
     * @return true if user has decided to show useful ads
     */
    boolean isShowUsefulAds();

    /**
     * Sets show useful ads filter enabled
     *
     * @param value true if user has decided to show useful ads
     */
    void setShowUsefulAds(boolean value);

    /**
     * @return list of enabled filter ids
     */
    List<Integer> getEnabledFilterIds();

	/**
	 * Is social media widgets filter enabled
	 */
	boolean isSocialMediaWidgetsFilterEnabled();

	/**
	 * Sets social media widgets filter enabled
	 */
	void setSocialMediaWidgetsFilterEnabled(boolean value);

	/**
	 * Sets spyware filter enabled
	 */
	void setSpywareFilterEnabled(boolean value);

	/**
	 * Is spyware filter enabled
	 */
	boolean isSpywareFilterEnabled();

    /**
     * Applies new settings and filters
     */
    void applyNewSettings();
}
