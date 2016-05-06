package com.adguard.android.model;

import com.adguard.android.model.enums.MobileStatus;

import java.util.Date;

/**
 * Mobile application status response
 */
public class StatusCheckResult {

    private final MobileStatus status;
    private final Date expirationDate;
    private final Boolean subscription;
    private final String purchaseToken;
    private final String storeName;
	private final boolean trial;

	/**
     * Creates an instance of MobileStatusResponse
     *
	 * @param status         Status
	 * @param expirationDate License expiration date (optional)
	 * @param purchaseToken  Purchase token
	 * @param trial
	 */
    public StatusCheckResult(MobileStatus status, Date expirationDate, Boolean subscription, String purchaseToken, String storeName, boolean trial) {
        this.status = status;
        this.expirationDate = expirationDate;
        this.subscription = subscription;
        this.purchaseToken = purchaseToken;
        this.storeName = storeName;
        this.trial = trial;
    }

    /**
     * @return Application status
     */
    public MobileStatus getStatus() {
        return status;
    }

    /**
     * @return License expiration date or null
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * @return true if it is store subscription and not a license key
     */
    public Boolean isSubscription() {
        return subscription;
    }

    /**
     * @return purchase token or null
     */
    public String getPurchaseToken() {
        return purchaseToken;
    }

    /**
     * @return store name like Google, Amazon, etc.
     */
    public String getStoreName() {
        return storeName;
    }

	/**
	 * @return license trial
	 */
	public boolean isTrial() {
		return trial;
	}
}
