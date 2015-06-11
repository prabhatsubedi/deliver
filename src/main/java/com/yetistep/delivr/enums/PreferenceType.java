package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/12/14
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public enum PreferenceType {
    /* Currency And Tax */
    CURRENCY,
    DELIVERY_FEE_VAT,           //System Vat
    ENABLE_FREE_REGISTER,
    TDS_PERCENTAGE,

    /* Reward Configuration */
    MAX_REFERRED_FRIENDS_COUNT, //Referral Limit
    REFERRAL_REWARD_AMOUNT,     //Referrer reward
    REFEREE_REWARD_AMOUNT,      //Referred User Reward
    NORMAL_USER_BONUS_AMOUNT,   //User sign up reward

    /* Rating Configuration */
    DBOY_DEFAULT_RATING,        //Shopper Minimum Rating
    CUSTOMER_DEFAULT_RATING,    //Customer Minimum Rating

    /* Support configuration */
    HELPLINE_NUMBER,
    CUSTOMER_CARE_EMAIL,
    ADMIN_EMAIL,
    ACCOUNT_EMAIL,
    SUPPORT_EMAIL,
    SMS_PROVIDER,     //Sparrow SMS 1, Twilio SMS 2
    SMS_COUNTRY_CODE,

    /* Company Information */
    COMPANY_NAME,
    COMPANY_LOGO,
    COMPANY_ADDRESS,
    CONTACT_NO,
    COMPANY_EMAIL,
    COMPANY_WEBSITE,
    VAT_NO,
    REGISTRATION_NO,
    APPLICATION_NAME,

    /* Version Configuration */
    ANDROID_APP_VER_NO,
    WEB_APP_VER_NO,
    IOS_APP_VER_NO,

    /* Default Image */
    DEFAULT_IMG_ITEM,
    DEFAULT_IMG_CATEGORY,
    DEFAULT_IMG_SEARCH,
    REFERRAL_FACEBOOK_IMG,
    LOGO_FOR_PDF_EMAIL,

    /* Wallet Configuration */
    MINIMUM_TRANSFERABLE_AMOUNT,
    INR_CONVERSION_RATE,
    TRANSFER_BONUS_PERCENT,

    /* Distance Selection */
    AIR_TO_ROUTE_DISTANCE_FACTOR,
    AIR_OR_ACTUAL_DISTANCE_SWITCH,  //AIR_DISTANCE = 0, ACTUAL_DISTANCE = 1

    /* Time, Distance and Charge Configuration */
    DBOY_ADDITIONAL_PER_KM_CHARGE,  //Per KM Charge for Dark Mile (Shopper to Store)
    DBOY_PER_KM_CHARGE_UPTO_NKM,    //Per KM Charge for Default Distance to Customer
    DBOY_PER_KM_CHARGE_ABOVE_NKM,   //Per KM Charge above Default Distance to Customer

    SURGE_FACTOR_7AM_9PM,
    SURGE_FACTOR_9_10PM,
    SURGE_FACTOR_10_11PM,
    SURGE_FACTOR_11PM_7AM,

    TIME_TO_TRAVEL_ONE_KM_ON_FOOT,
    TIME_TO_TRAVEL_ONE_KM_ON_BICYCLE,
    TIME_TO_TRAVEL_ONE_KM_ON_MOTORBIKE,
    TIME_TO_TRAVEL_ONE_KM_ON_CAR,
    TIME_TO_TRAVEL_ONE_KM_ON_TRUCK,
    TIME_TO_TRAVEL_ONE_KM_ON_OTHERS,
    TIME_AT_STORE,

    ADDITIONAL_KM_FREE_LIMIT,       //Unpaid KM for Shopper
    DEFAULT_NKM_DISTANCE,           //Default Charged Distance from Customer to Store
    MAX_ORDER_SERVING_DISTANCE,     //Maximum order serving distance in KM

    /* Order Processing Configuration*/
    DEVIATION_IN_TIME,              //Order Acceptance Deviation Time in Min
    ORDER_REQUEST_TIMEOUT_IN_MIN,
    ORDER_MAX_AMOUNT,
    REPROCESS_EXTRA_TIME,           //Reprocessed Order Acceptance Deviation Time in Min
    ACCEPTANCE_RADIUS,              //Order Status Change Radius
    DBOY_GRESS_TIME,                //Grace Time for Order Delivery given to Shopper in Min
    PROFIT_CHECK_FLAG,              //Check Profit = 1(Yes), Don't Check Profit = 2 (No)
    LOCATION_UPDATE_TIMEOUT_IN_MIN,

    /* Profit And Commission Configuration */
    RESERVED_COMM_PER_BY_SYSTEM,
    DBOY_COMMISSION,                //Shopper Commission %
    DBOY_MIN_AMOUNT,                //Minimum Amount Given to Shopper
    MINIMUM_PROFIT_PERCENTAGE,      //Minimum Profit Percentage for Order Acceptance
    DEDUCTION_PERCENT,              //Deduction in case of Delayed Delivery from Shopper Earning
    DISCOUNT_ON_DELIVERY_FEE

}
