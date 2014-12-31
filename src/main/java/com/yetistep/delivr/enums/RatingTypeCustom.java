package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/31/14
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class RatingTypeCustom extends PersistentEnumUserType<RatingType> {
    @Override
    public Class<RatingType> returnedClass() {
        return RatingType.class;
    }

}
