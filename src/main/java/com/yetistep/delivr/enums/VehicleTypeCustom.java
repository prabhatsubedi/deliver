package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/2/14
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class VehicleTypeCustom extends PersistentEnumUserType<VehicleType> {
    @Override
    public Class<VehicleType> returnedClass() {
        return VehicleType.class;
    }
}
