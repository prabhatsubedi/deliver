package com.yetistep.delivr.model.mobile;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/19/15
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddressDto {
    private Boolean mobileValidate;
    private String verificationCode;

    public Boolean getMobileValidate() {
        return mobileValidate;
    }

    public void setMobileValidate(Boolean mobileValidate) {
        this.mobileValidate = mobileValidate;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
