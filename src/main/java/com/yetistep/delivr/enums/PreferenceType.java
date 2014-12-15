package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/12/14
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public enum PreferenceType {
    CURRENCY,
    //Delivery Boy Preferences
    DBOY_ADDITIONAL_PER_KM_CHARGE, DBOY_PER_KM_CHARGE_UPTO_2KM, DBOY_PER_KM_CHARGE_ABOVE_2KM, RESERVED_COMM_PER_BY_SYSTEM,
    //DBOY Surge Factor
    SURGE_FACTOR_7AM_9PM, SURGE_FACTOR_9_10PM, SURGE_FACTOR_10_11PM, SURGE_FACTOR_11PM_7AM,
    DBOY_COMMISSION, DBOY_MIN_AMOUNT,MERCHANT_VAT, MERCHANT_SERVICE_CHARGE,

    ANDROID_APP_VER_NO, WEB_APP_VER_NO;
}
