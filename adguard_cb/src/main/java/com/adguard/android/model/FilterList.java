/*
 This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2018 AdGuard Content Blocker. All rights reserved.

 AdGuard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 AdGuard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.model;

import com.adguard.android.commons.ProductVersion;

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
