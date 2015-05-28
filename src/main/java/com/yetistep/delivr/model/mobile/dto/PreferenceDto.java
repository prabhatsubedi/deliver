package com.yetistep.delivr.model.mobile.dto;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/26/15
 * Time: 4:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreferenceDto {
    private String helplineNumber;
    private String customerCareEmail;
    private String acceptanceRadius;
    private BigDecimal refereeRewardAmount;
    private String currency;

    public String getHelplineNumber() {
        return helplineNumber;
    }

    public void setHelplineNumber(String helplineNumber) {
        this.helplineNumber = helplineNumber;
    }

    public String getCustomerCareEmail() {
        return customerCareEmail;
    }

    public void setCustomerCareEmail(String customerCareEmail) {
        this.customerCareEmail = customerCareEmail;
    }

    public String getAcceptanceRadius() {
        return acceptanceRadius;
    }

    public void setAcceptanceRadius(String acceptanceRadius) {
        this.acceptanceRadius = acceptanceRadius;
    }

    public BigDecimal getRefereeRewardAmount() {
        return refereeRewardAmount;
    }

    public void setRefereeRewardAmount(BigDecimal refereeRewardAmount) {
        this.refereeRewardAmount = refereeRewardAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
