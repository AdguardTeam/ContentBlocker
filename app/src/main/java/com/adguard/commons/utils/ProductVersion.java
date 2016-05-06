package com.adguard.commons.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

@SuppressWarnings("UnusedDeclaration")
public class ProductVersion implements Comparable<ProductVersion> {

    private final static int MAX_VERSION = 100;
    private int major;
    private int minor;
    private int revision;
    private int build;

    public ProductVersion() {
    }

    public ProductVersion(int major, int minor, int revision, int build) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.build = build;
    }

    public ProductVersion(String version) {
        if (StringUtils.isEmpty(version)) {
            return;
        }

        String[] parts = StringUtils.split(version, ".");

        if (parts.length >= 4) {
            build = parseVersionPart(parts[3]);
        }
        if (parts.length >= 3) {
            revision = parseVersionPart(parts[2]);
        }
        if (parts.length >= 2) {
            minor = parseVersionPart(parts[1]);
        }
        if (parts.length >= 1) {
            major = parseVersionPart(parts[0]);
        }
    }

    private static int parseVersionPart(String part) {
        int versionPart = NumberUtils.toInt(part, 0);
        if (versionPart < 0) {
            versionPart = 0;
        }
        return versionPart;
    }

    /**
     * Increments product version
     */
    public void increment() {
        setBuild(getBuild() + 1);
        if (getBuild() >= MAX_VERSION) {
            setRevision(getRevision() + 1);
            setBuild(0);
            if (getRevision() >= MAX_VERSION) {
                setMinor(getMinor() + 1);
                setRevision(0);
                if (getMinor() >= MAX_VERSION) {
                    setMajor(getMajor() + 1);
                    setMinor(0);
                }
            }
        }
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    public String getShortVersionString() {
        return major + "." + minor;
    }

    public String getShortWithRevisionString() {
        return major + "." + minor + "." + revision;
    }

    public String getLongVersionString() {
        return major + "." + minor + "." + revision + "." + build;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(major);
        sb.append(".");
        sb.append(minor);
        if (revision > 0 || build > 0) {
            sb.append(".");
            sb.append(revision);
        }
        if (build > 0) {
            sb.append(".");
            sb.append(build);
        }
        return sb.toString();
    }

    @Override
    public int compareTo(ProductVersion o) {
        if (getMajor() > o.getMajor()) {
            return 1;
        } else if (getMajor() < o.getMajor()) {
            return -1;
        }

        if (getMinor() > o.getMinor()) {
            return 1;
        } else if (getMinor() < o.getMinor()) {
            return -1;
        }

        if (getRevision() > o.getRevision()) {
            return 1;
        } else if (getRevision() < o.getRevision()) {
            return -1;
        }

		if (getBuild() > o.getBuild()) {
			return 1;
		} else if (getBuild() < o.getBuild()) {
			return -1;
		}

        return 0;
    }
}
