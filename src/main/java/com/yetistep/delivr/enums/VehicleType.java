package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/21/14
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public enum VehicleType implements PersistentEnum {
    WALKING(1), BICYCLE(2) , MOTORBIKE(3), FOUR_WHEELER(4), TRUCK(5);

    private final int id;

    private VehicleType(int id){
        this.id = id;
    }

    public String toStr(){
        return this.toString();
    }

    public static VehicleType fromString(String arg){
        if(arg.equalsIgnoreCase("walking"))
            return WALKING;
        else if(arg.equalsIgnoreCase("bicycle"))
            return BICYCLE;
        else if(arg.equalsIgnoreCase("motorbike"))
            return MOTORBIKE;
        else if(arg.equalsIgnoreCase("four_wheeler"))
            return FOUR_WHEELER;
        else
            return TRUCK;
    }

    public Integer toInt() {
        return id;
    }

    public static VehicleType fromInt(Integer arg){
        if(arg.equals(1))
            return WALKING;
        else if(arg.equals(2))
            return BICYCLE;
        else if(arg.equals(3))
            return MOTORBIKE;
        else if(arg.equals(4))
            return FOUR_WHEELER;
        else
            return TRUCK;
    }

    @Override
    public int getId() {
        return id;
    }
}
