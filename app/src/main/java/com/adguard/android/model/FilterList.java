package com.adguard.android.model;

import com.adguard.commons.utils.ProductVersion;

import java.util.Date;

/**
 * Represents filter list
 */
public class FilterList {

    private int filterId;
    private String name;
    private String description;
    private boolean enabled;
    private String version;
    private Date timeUpdated;
    private Date lastTimeDownloaded;
    private int displayOrder;

    /**
     * @return Filter identifier
     */
    public int getFilterId() {
        return filterId;
    }

    /**
     * @param filterId Filter identifier
     */
    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }

    /**
     * @return Filter name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Filter name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Filter description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description Filter description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return true if filter is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled true if filter is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return Filter version
     */
    public ProductVersion getVersion() {
        return new ProductVersion(version);
    }

    /**
     * @param version Filter version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return Time when filter version was updated
     */
    public Date getTimeUpdated() {
        return timeUpdated;
    }

    /**
     * @param timeUpdated Time when filter version was updated
     */
    public void setTimeUpdated(Date timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    /**
     * @return Last time when filter was successfully downloaded
     */
    public Date getLastTimeDownloaded() {
        return lastTimeDownloaded;
    }

    /**
     * @param lastTimeDownloaded Last time when filter was successfully downloaded
     */
    public void setLastTimeDownloaded(Date lastTimeDownloaded) {
        this.lastTimeDownloaded = lastTimeDownloaded;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
