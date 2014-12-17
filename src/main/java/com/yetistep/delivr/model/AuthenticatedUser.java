package com.yetistep.delivr.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/11/14
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthenticatedUser extends org.springframework.security.core.userdetails.User{
    private Integer userId;
    private Integer merchantId;
    private String fullName;
    private String mobileNumber;

    public AuthenticatedUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public AuthenticatedUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
