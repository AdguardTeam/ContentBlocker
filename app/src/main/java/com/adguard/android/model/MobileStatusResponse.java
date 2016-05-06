package com.adguard.android.model;

import com.adguard.android.model.enums.MobileStatus;

import java.util.Date;

/**
 * Mobile application status response
 */
public class MobileStatusResponse {

    private String licenseKey;
    private String countryCode;
    private MobileStatus status;
    private Date expirationDate;

    /**
     * @return License key
     */
    public String getLicenseKey() {
        return licenseKey;
    }

    /**
     * @return Country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @return Application status
     */
    public MobileStatus getStatus() {
        return status;
    }

    /**
     * @return Expiration date
     */
    public Date getExpirationDate() {
        return expirationDate;
    }
}
