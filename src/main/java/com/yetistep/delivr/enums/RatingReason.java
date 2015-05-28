package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/2/15
 * Time: 12:59 PM
 * To change this template use File | Settings | File Templates.
 */
public enum RatingReason {
    PAYMENT_ISSUE, DELIVERY_ISSUE, TIMING_ISSUE, ATTITUDE_ISSUE;

    public String toStr() {
        return this.toString();
    }

    public Integer toInt() {
        return this.ordinal();
    }

    public static RatingReason fromInt(Integer arg) {
        switch (arg) {
            case 0:
                return PAYMENT_ISSUE;
            case 1:
                return DELIVERY_ISSUE;
            case 2:
                return TIMING_ISSUE;
            default:
                return ATTITUDE_ISSUE;
        }
    }
}
