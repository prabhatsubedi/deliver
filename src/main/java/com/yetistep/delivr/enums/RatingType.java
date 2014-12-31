package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/31/14
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public enum RatingType implements PersistentEnum {
    POOR(1),
    AVERAGE(3),
    GOOD(5);


    private final int id;

    RatingType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public static RatingType fromInt(int arg){
        if(arg == 1)
            return POOR;
        else if(arg == 3)
            return AVERAGE;
        else
            return GOOD;
    }

}
