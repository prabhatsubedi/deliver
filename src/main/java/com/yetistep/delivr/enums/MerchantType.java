package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/21/14
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public enum MerchantType implements PersistentEnum{
    INDIVIDUAL(1), CORPORATE(2);

    private final int id;

    MerchantType(int id){
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public static MerchantType fromInt(int arg){
        if(arg == 1)
            return INDIVIDUAL;
        else
            return CORPORATE;
    }
}
