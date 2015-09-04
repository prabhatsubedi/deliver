package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 9/4/15
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public enum CanceledItemStatus {

    InStock,BadDebt;

    public static CanceledItemStatus fromInt(int arg) {
        switch (arg) {
            case 0:
                return InStock;
            case 1:
                return BadDebt;
            default:
                return InStock;
        }
    }

}
