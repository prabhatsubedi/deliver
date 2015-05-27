package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/21/14
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public enum DBoyStatus {
    NOT_AVAILABLE, FREE, BUSY;

    public String toStr() {
        return this.toString();
    }

    public Integer toInt() {
        return this.ordinal();
    }

    public static DBoyStatus fromInt(Integer arg) {
        switch (arg) {
            case 0:
                return NOT_AVAILABLE;
            case 1:
                return FREE;
            default:
                return BUSY;
        }
    }
}
