package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/26/14
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantTypeCustom extends PersistentEnumUserType<MerchantType>{

    @Override
    public Class<MerchantType> returnedClass() {
        return MerchantType.class;
    }

}
