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
    private String userName;
    private String userLocation;

    public AuthenticatedUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String userName, String userLocation) {
        super(username, password, authorities);
        this.userName = userName;
        this.userLocation = userLocation;
    }

    public AuthenticatedUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String userName, String userLocation) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.userName = userName;
        this.userLocation = userLocation;
    }


    public String getUserName() {
        return userName;
    }

    public String getUserLocation() {
        return userLocation;
    }
}
