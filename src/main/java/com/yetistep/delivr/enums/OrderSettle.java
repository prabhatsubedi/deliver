package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/21/14
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public enum OrderSettle implements PersistentEnum {
    SETTLED(1), UNSETTLED(2);

    private final int id;

    OrderSettle(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public static OrderSettle fromInt(int arg) {
        if (arg == 1)
            return SETTLED;
        return UNSETTLED;
    }
}
