package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/10/15
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public enum PushNotificationRedirect {
    ORDER(":order"), DELIVR(":delivr"), PLAYSTORE(":playstore"), RECEIPT(":receipt");

    private String redirectTo;

    PushNotificationRedirect(String redirectTo){
        this.redirectTo = redirectTo;
    }

    public String getRedirectTo() {
        return redirectTo;
    }
}
