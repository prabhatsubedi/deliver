package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/12/14
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public enum PreferenceType {
    CURRENCY,TDS_PERCENTAGE,
    //Delivery Boy Preferences
    DBOY_ADDITIONAL_PER_KM_CHARGE, DBOY_PER_KM_CHARGE_UPTO_NKM, DBOY_PER_KM_CHARGE_ABOVE_NKM, DEFAULT_NKM_DISTANCE, RESERVED_COMM_PER_BY_SYSTEM,
    //DBOY Surge Factor
    SURGE_FACTOR_7AM_9PM, SURGE_FACTOR_9_10PM, SURGE_FACTOR_10_11PM, SURGE_FACTOR_11PM_7AM,

    DBOY_COMMISSION, DBOY_MIN_AMOUNT, DELIVERY_FEE_VAT, MINIMUM_PROFIT_PERCENTAGE, ADDITIONAL_KM_FREE_LIMIT,

    //Time to travel for delivery boy
    TIME_TO_TRAVEL_ONE_KM_ON_FOOT, TIME_TO_TRAVEL_ONE_KM_ON_BICYCLE, TIME_TO_TRAVEL_ONE_KM_ON_MOTORBIKE,
    TIME_TO_TRAVEL_ONE_KM_ON_CAR, TIME_TO_TRAVEL_ONE_KM_ON_TRUCK, TIME_TO_TRAVEL_ONE_KM_ON_OTHERS,

    DEVIATION_IN_TIME, REPROCESS_EXTRA_TIME, TIME_AT_STORE,

    /*AIR_DISTANCE = 0, ACTUAL_DISTANCE = 1*/
    AIR_TO_ROUTE_DISTANCE_FACTOR, AIR_OR_ACTUAL_DISTANCE_SWITCH, ORDER_REQUEST_TIMEOUT_IN_MIN, ORDER_MAX_AMOUNT,

    //Customer Preferences
    REFERRAL_REWARD_AMOUNT, REFEREE_REWARD_AMOUNT, MAX_REFERRED_FRIENDS_COUNT,NORMAL_USER_BONUS_AMOUNT,
    DBOY_GRESS_TIME, DBOY_DEFAULT_RATING, CUSTOMER_DEFAULT_RATING, DEDUCTION_PERCENT, PROFIT_CHECK_FLAG,
    LOCATION_UPDATE_TIMEOUT_IN_MIN,ENABLE_FREE_REGISTER,ADMIN_EMAIL,ACCOUNT_EMAIL,SUPPORT_EMAIL,COMPANY_NAME,COMPANY_LOGO,
    COMPANY_ADDRESS,CONTACT_NO,COMPANY_EMAIL,COMPANY_WEBSITE,VAT_NO,REGISTRATION_NO,APPLICATION_NAME,

    //Default Images
    DEFAULT_IMG_ITEM, DEFAULT_IMG_CATEGORY, DEFAULT_IMG_SEARCH,REFERRAL_FACEBOOK_IMG,

    ANDROID_APP_VER_NO, WEB_APP_VER_NO, HELPLINE_NUMBER, ACCEPTANCE_RADIUS, CUSTOMER_CARE_EMAIL,

    MINIMUM_TRANSFERABLE_AMOUNT, INR_CONVERSION_RATE, TRANSFER_BONUS_PERCENT
}
