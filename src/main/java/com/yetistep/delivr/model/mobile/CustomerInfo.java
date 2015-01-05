package com.yetistep.delivr.model.mobile;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/25/14
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerInfo {
    private String fbToken;
    private Long clientId;
    private Integer userId;

    public String getFbToken() {
        return fbToken;
    }

    public void setFbToken(String fbToken) {
        this.fbToken = fbToken;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
