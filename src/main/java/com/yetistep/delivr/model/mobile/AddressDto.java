package com.yetistep.delivr.model.mobile;

import com.yetistep.delivr.model.AddressEntity;
import com.yetistep.delivr.model.UserEntity;

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
    private Boolean validatedByUser;
    private AddressEntity address;
    private UserEntity user;

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

    public AddressEntity getAddress() {
        return address;
    }

    public Boolean getValidatedByUser() {
        return validatedByUser;
    }

    public void setValidatedByUser(Boolean validatedByUser) {
        this.validatedByUser = validatedByUser;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
