package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/21/14
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public enum VehicleType implements PersistentEnum {
    ON_FOOT(1), BICYCLE(2) , MOTORBIKE(3), CAR(4), TRUCK(5), OTHERS(6);

    private final int id;

    private VehicleType(int id){
        this.id = id;
    }

    public String toStr(){
        return this.toString();
    }

    public static VehicleType fromString(String arg){
        if(arg.equalsIgnoreCase("on_foot"))
            return ON_FOOT;
        else if(arg.equalsIgnoreCase("bicycle"))
            return BICYCLE;
        else if(arg.equalsIgnoreCase("motorbike"))
            return MOTORBIKE;
        else if(arg.equalsIgnoreCase("car"))
            return CAR;
        else if(arg.equalsIgnoreCase("truck"))
            return TRUCK;
        else
            return OTHERS;
    }

    public Integer toInt() {
        return id;
    }

    public static VehicleType fromInt(Integer arg){
        if(arg.equals(1))
            return ON_FOOT;
        else if(arg.equals(2))
            return BICYCLE;
        else if(arg.equals(3))
            return MOTORBIKE;
        else if(arg.equals(4))
            return CAR;
        else if(arg.equals(5))
            return TRUCK;
        else
            return OTHERS;
    }

    @Override
    public int getId() {
        return id;
    }
}
