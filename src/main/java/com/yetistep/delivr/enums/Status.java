package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/10/14
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Status {
    //Do not Order Change [ 0,  1, 2, 3]
    UNVERIFIED, VERIFIED, ACTIVE, INACTIVE;

    public Integer toInt() {
        return this.ordinal();
    }

    public static Status fromInt(Integer arg) {
        switch (arg) {
            case 0:
                return UNVERIFIED;
            case 1:
                return VERIFIED;
            case 2:
                return ACTIVE;
            case 3:
                return INACTIVE;
            default:
                return UNVERIFIED;
        }
    }
}
