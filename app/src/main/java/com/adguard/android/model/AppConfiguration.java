package com.adguard.android.model;

import java.util.List;

/**
 * Represents global application configuration.
 * This class is used to send application configuration to backend
 * along with feedback message.
 */
@SuppressWarnings("UnusedDeclaration")
public class AppConfiguration {

    private String applicationId;
    private String versionName;
    private boolean premium;
    private boolean trial;
    private boolean filtersAutoUpdateEnabled;
    private boolean autoStartEnabled;
    private boolean showUsefulAds;
    private List<Integer> enabledFilterIds;

    private String deviceName;
    private String androidVersion;
    private String kernelVersion;

    private String referrer;
    private String installerPackage;
    private String sourceApk;
    private String environment;
    private String appLanguage;
    private List<String> installedPackages;

    /**
     * @return Application unique identifier
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * @param applicationId Application unique identifier
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * @return application version name
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * Sets application version name
     *
     * @param versionName Version
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * @return true if filters autoupdate is enabled
     */
    public boolean isFiltersAutoUpdateEnabled() {
        return filtersAutoUpdateEnabled;
    }

    /**
     * @param filtersAutoUpdateEnabled true if filters autoupdate is enabled
     */
    public void setFiltersAutoUpdateEnabled(boolean filtersAutoUpdateEnabled) {
        this.filtersAutoUpdateEnabled = filtersAutoUpdateEnabled;
    }

    /**
     * @return true if application autostart is enabled
     */
    public boolean isAutoStartEnabled() {
        return autoStartEnabled;
    }

    /**
     * @param autoStartEnabled true if application autostart is enabled
     */
    public void setAutoStartEnabled(boolean autoStartEnabled) {
        this.autoStartEnabled = autoStartEnabled;
    }

    /**
     * @return true if useful ads filter is enabled
     */
    public boolean isShowUsefulAds() {
        return showUsefulAds;
    }

    /**
     * @param showUsefulAds true if useful ads filter is enabled
     */
    public void setShowUsefulAds(boolean showUsefulAds) {
        this.showUsefulAds = showUsefulAds;
    }

    /**
     * @param enabledFilterIds List of enabled filters
     */
    public void setEnabledFilterIds(List<Integer> enabledFilterIds) {
        this.enabledFilterIds = enabledFilterIds;
    }

    /**
     * @return List of enabled filters
     */
    public List<Integer> getEnabledFilterIds() {
        return enabledFilterIds;
    }

    /**
     * @return true if app is premium
     */
    public boolean isPremium() {
        return premium;
    }

    /**
     * @param premium true if app is premium
     */
    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    /**
     * @return license trial
     */
    public boolean isTrial() {
        return trial;
    }

    /**
     * @param trial license trial
     */
    public void setTrial(boolean trial) {
        this.trial = trial;
    }

    /**
     * @return Device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * @param deviceName Device name
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * @return Android version
     */
    public String getAndroidVersion() {
        return androidVersion;
    }

    /**
     * @param androidVersion Android version
     */
    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    /**
     * @return Kernel version
     */
    public String getKernelVersion() {
        return kernelVersion;
    }

    /**
     * @param kernelVersion Kernel version
     */
    public void setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
    }

    /**
     * @return Referrer (got from INSTALL_REFERRER receiver)
     */
    public String getReferrer() {
        return referrer;
    }

    /**
     * @param referrer Referrer (got from INSTALL_REFERRER receiver)
     */
    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    /**
     * @return Installer package
     */
    public String getInstallerPackage() {
        return installerPackage;
    }

    /**
     * @param installerPackage Installer package
     */
    public void setInstallerPackage(String installerPackage) {
        this.installerPackage = installerPackage;
    }

    /**
     * @return Source apk file name
     */
    public String getSourceApk() {
        return sourceApk;
    }

    /**
     * @param sourceApk Source apk file name
     */
    public void setSourceApk(String sourceApk) {
        this.sourceApk = sourceApk;
    }

    /**
     * @return Build environment (prod/google/dev)
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * @param environment Build environment (prod/google/dev)
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * @param appLanguage Application language
     */
    public void setAppLanguage(String appLanguage) {
        this.appLanguage = appLanguage;
    }

    /**
     * @return Application language
     */
    public String getAppLanguage() {
        return appLanguage;
    }
}
