package com.yetistep.delivr.util;

import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.AuthenticatedUser;
import com.yetistep.delivr.model.RoleEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/16/14
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class SessionManager {
    private static final String BLANK = "";

    public static AuthenticatedUser getPrincipal() {
        return (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String getUserName() {
        return getPrincipal().getUsername();
    }

    public static Role getRole() {
        String role = BLANK;
        for (Iterator iterator = getPrincipal().getAuthorities().iterator(); iterator.hasNext(); ) {
            //Currently Just One Role Assigned
            role = String.valueOf(iterator.next());
            break;
        }
        return Role.valueOf(role);
    }

    public static String getFullName() {
        return getPrincipal().getFullName();
    }

    public static String getMobileNumber() {
        return getPrincipal().getMobileNumber();
    }

    public static Integer getMerchantId() {
        return getPrincipal().getMerchantId();
    }

    public static Integer getUserId() {
        return getPrincipal().getUserId();

    }

    public static boolean isAnonymousUser(){
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser");
    }

}
