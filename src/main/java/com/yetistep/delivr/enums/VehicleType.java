package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/21/14
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public enum VehicleType implements PersistentEnum {
    ON_FOOT(1), BICYCLE(2), MOTORBIKE(3), CAR(4), TRUCK(5), OTHERS(6);

    private final int id;

    private VehicleType(int id) {
        this.id = id;
    }

    public String toStr() {
        return this.toString();
    }

    public static VehicleType fromString(String arg) {
        switch (arg.toLowerCase()) {
            case "on_foot":
                return ON_FOOT;
            case "bicycle":
                return BICYCLE;
            case "motorbike":
                return MOTORBIKE;
            case "car":
                return CAR;
            case "truck":
                return TRUCK;
            default:
                return OTHERS;
        }
    }

    public Integer toInt() {
        return id;
    }

    public static VehicleType fromInt(Integer arg) {
        switch (arg) {
            case 1:
                return ON_FOOT;
            case 2:
                return BICYCLE;
            case 3:
                return MOTORBIKE;
            case 4:
                return CAR;
            case 5:
                return TRUCK;
            default:
                return OTHERS;
        }
    }

    @Override
    public int getId() {
        return id;
    }
}
