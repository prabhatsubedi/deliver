package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/26/14
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderSettleCustom extends PersistentEnumUserType<OrderSettle>{

    @Override
    public Class<OrderSettle> returnedClass() {
        return OrderSettle.class;
    }

}
