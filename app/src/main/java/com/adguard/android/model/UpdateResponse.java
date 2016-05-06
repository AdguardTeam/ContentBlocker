package com.adguard.android.model;

/**
 * Update request response.
 */
public class UpdateResponse {
	private String version;
	private String updateURL;
	private String updatePageURL;
	private String releaseNotes;
	private String moreInfoURL;
	private boolean forced;
	private boolean isMajor;
	private String dotnetVersion;
	private String dotnetDownloadLink;
	private String dotnetOfficialLink;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUpdateURL() {
		return updateURL;
	}

	public void setUpdateURL(String updateURL) {
		this.updateURL = updateURL;
	}

	public String getUpdatePageURL() {
		return updatePageURL;
	}

	public void setUpdatePageURL(String updatePageURL) {
		this.updatePageURL = updatePageURL;
	}

	public String getReleaseNotes() {
		return releaseNotes;
	}

	public void setReleaseNotes(String releaseNotes) {
		this.releaseNotes = releaseNotes;
	}

	public String getMoreInfoURL() {
		return moreInfoURL;
	}

	public void setMoreInfoURL(String moreInfoURL) {
		this.moreInfoURL = moreInfoURL;
	}

	public boolean isForced() {
		return forced;
	}

	public void setForced(boolean forced) {
		this.forced = forced;
	}

	public boolean getIsMajor() {
		return isMajor;
	}

	public void setIsMajor(boolean isMajor) {
		this.isMajor = isMajor;
	}

	public String getDotnetVersion() {
		return dotnetVersion;
	}

	public void setDotnetVersion(String dotnetVersion) {
		this.dotnetVersion = dotnetVersion;
	}

	public String getDotnetDownloadLink() {
		return dotnetDownloadLink;
	}

	public void setDotnetDownloadLink(String dotnetDownloadLink) {
		this.dotnetDownloadLink = dotnetDownloadLink;
	}

	public String getDotnetOfficialLink() {
		return dotnetOfficialLink;
	}

	public void setDotnetOfficialLink(String dotnetOfficialLink) {
		this.dotnetOfficialLink = dotnetOfficialLink;
	}
}
