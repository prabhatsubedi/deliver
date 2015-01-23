package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/23/15
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public enum DistanceType {
    AIR_DISTANCE, ACTUAL_DISTANCE;

    public String toStr(){
        return this.toString();
    }

    public Integer toInt() {
        return this.ordinal();
    }

    public static DistanceType fromInt(Integer arg){
        switch (arg){
            case 1: return ACTUAL_DISTANCE;
            default: return AIR_DISTANCE;
        }
    }
}
