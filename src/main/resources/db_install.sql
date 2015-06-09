/*12th dEC BY Sagar Sapkota*/
ALTER TABLE stores DROP COLUMN locality;
ALTER TABLE stores DROP COLUMN return_policy;
ALTER TABLE stores DROP COLUMN delivery_fee;
ALTER TABLE stores DROP COLUMN promo_code;
ALTER TABLE stores DROP COLUMN vat;
ALTER TABLE stores DROP COLUMN service_charge;
TRUNCATE TABLE preferences;
INSERT INTO preferences (pref_key,VALUE) VALUES
('CURRENCY','$'),
('DBOY_ADDITIONAL_PER_KM_CHARGE','5'),
('DBOY_PER_KM_CHARGE_UPTO_NKM','50'),
('DBOY_PER_KM_CHARGE_ABOVE_NKM','30'),
('RESERVED_COMM_PER_BY_SYSTEM','50'),
('SURGE_FACTOR_7AM_9PM','1'),
('SURGE_FACTOR_9_10PM','2'),
('SURGE_FACTOR_10_11PM','3'),
('SURGE_FACTOR_11PM_7AM','4'),
('DBOY_COMMISSION','80'),
('DBOY_MIN_AMOUNT','50'),
('MERCHANT_VAT','13'),
('MERCHANT_SERVICE_CHARGE','10'),
('ANDROID_APP_VER_NO','0.1'),
('WEB_APP_VER_NO','0.1'),
('TIME_TO_TRAVEL_ONE_KM_ON_FOOT','15'),
('TIME_TO_TRAVEL_ONE_KM_ON_BICYCLE','6'),
('TIME_TO_TRAVEL_ONE_KM_ON_MOTORBIKE','5'),
('TIME_TO_TRAVEL_ONE_KM_ON_CAR','5'),
('TIME_TO_TRAVEL_ONE_KM_ON_TRUCK','10'),
('TIME_TO_TRAVEL_ONE_KM_ON_OTHERS','10'),
('DEVIATION_IN_TIME','15'),
('TIME_AT_STORE','30'),
('DELIVERY_FEE_VAT','0'),
('MINIMUM_PROFIT_PERCENTAGE','10'),
('ADDITIONAL_KM_FREE_LIMIT','1');


ALTER TABLE items DROP COLUMN listing_days;
ALTER TABLE items DROP COLUMN available_start_time;
ALTER TABLE items DROP COLUMN available_end_time;
ALTER TABLE stores_brands DROP COLUMN opening_time;
ALTER TABLE stores_brands DROP COLUMN closing_time;
ALTER TABLE items DROP COLUMN multi_select_offer;
ALTER TABLE items DROP COLUMN single_select_offer;
ALTER TABLE orders DROP COLUMN transportaion_charge;
ALTER TABLE delivery_boys DROP COLUMN latitude;
ALTER TABLE delivery_boys DROP COLUMN longitude;
ALTER TABLE items_attributes DROP COLUMN item_id;
ALTER TABLE categories DROP COLUMN store_id;

ALTER TABLE `orders` DROP `customer_chargeable_distance` ,
DROP `delivery_boy_share` ,
DROP `delivery_charge` ,
DROP `grand_total` ,
DROP `system_chargeable_distance` ,
DROP `system_service_charge` ,
DROP `system_share` ,
DROP `total_cost` ,
DROP `transportation_charge` ;
ALTER TABLE `items_orders` DROP `item_total` ;

############ 2015.01.21 ###################
ALTER TABLE address DROP INDEX UK_4t7y1821minhabn2kbl990ith;

########### 2015.01.22 #####################
ALTER TABLE items_attributes MODIFY COLUMN unit_price decimal(19,2) NULL;

UPDATE `preferences` SET `pref_key`='DBOY_PER_KM_CHARGE_UPTO_NKM' WHERE `id`='3';

UPDATE `preferences` SET `pref_key`='DBOY_PER_KM_CHARGE_ABOVE_NKM' WHERE `id`='4';

INSERT INTO preferences (pref_key,VALUE) VALUES
('DELIVERY_FEE_VAT','0'),
('MINIMUM_PROFIT_PERCENTAGE','10'),
('ADDITIONAL_KM_FREE_LIMIT','1'),
('DEFAULT_NKM_DISTANCE', 4);

ALTER TABLE orders MODIFY COLUMN order_verification_code VARCHAR(255) NULL DEFAULT NULL ;

/*###############2015.01.23########################*/
INSERT INTO preferences (pref_key,VALUE) VALUES ('AIR_TO_ROUTE_DISTANCE_FACTOR','1.5');

INSERT INTO preferences (pref_key,VALUE) VALUES ('AIR_OR_ACTUAL_DISTANCE_SWITCH','1');

/*####################2015.01.27########################*/
ALTER TABLE `items_orders` DROP COLUMN `custom_item`;

/*####################2015.01.28#########################*/
ALTER TABLE `custom_items` DROP COLUMN `vat`, DROP COLUMN `service_charge`;

INSERT INTO preferences (pref_key, value) VALUES ('ORDER_REQUEST_TIMEOUT_IN_MIN', '4');
DELETE FROM preferences WHERE pref_key IN ('MERCHANT_VAT', 'MERCHANT_SERVICE_CHARGE');

/* ################### 2015.01.29 ###################### */
ALTER TABLE courier_transaction DROP COLUMN actual_delivery_charge_with_vat;
INSERT INTO preferences (pref_key, value) VALUES ('ORDER_MAX_AMOUNT', '10000');

/* ############### 2015.01.30 ####################### */
 INSERT INTO preferences (pref_key, value) VALUES ('DEFAULT_IMG_ITEM', 'https://delivrtest.s3.amazonaws.com/default/item/def_item.png');

 /* ############## 2015.02.02 ####################### */
 INSERT INTO preferences (pref_key, value) VALUES ('DEFAULT_IMG_CATEGORY', 'https://delivrtest.s3.amazonaws.com/default/category/cat.jpg');

/*################ 2015-01-12 #########################*/
ALTER TABLE `cart` DROP COLUMN `order_id`;

/*################# 2015-02-16 ######################*/
INSERT INTO `reason_details` (`cancel_reason`, `status`) VALUES ('Customer Not Reachable', '1');
INSERT INTO `reason_details` (`cancel_reason`, `status`) VALUES ('Item Not Found', '1');
INSERT INTO `reason_details` (`cancel_reason`, `status`) VALUES ('Customer Denied to take order', '1');
INSERT INTO `reason_details` (`cancel_reason`, `status`) VALUES ('Customer Location Unidentified', '1');
INSERT INTO `reason_details` (`cancel_reason`, `status`) VALUES ('Order not accepted in given time', '1');
INSERT INTO `reason_details` (`cancel_reason`, `status`) VALUES ('Others', '1');


/*####################2015-02-19############################*/
ALTER TABLE `customers` CHANGE COLUMN `fb_token` `fb_token` LONGTEXT NULL DEFAULT NULL ;

ALTER TABLE `items` DROP `brandId`, DROP `closingTime`, DROP `openingTime`;

/*#######################2015-02-23#####################################*/
ALTER TABLE `dboy_order_history` CHANGE COLUMN `amount_earned` `amount_earned` DECIMAL(19,2) NULL DEFAULT NULL ;

/* ################ 2015-02-25 ####################################### */
ALTER TABLE users DROP INDEX UK_6dotkott2kjsp8vw4d0m25fb7;

/*#######################2015-02-26#####################################*/
INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('REPROCESS_EXTRA_TIME', '15');
ALTER TABLE `courier_transaction`
CHANGE COLUMN `customer_discount` `customer_discount` DECIMAL(16,2) NULL DEFAULT NULL ;

INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('HELPLINE_NUMBER', '9800000000');
INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('CUSTOMER_CARE_EMAIL', 'customercare@yetistep.com');
INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('ACCEPTANCE_RADIUS', '0');
/* ############# 2015-02-27 ######################### */
/* IF data not available in reason details then insert */
INSERT INTO `delivr_db`.`reason_details` (`cancel_reason`, `status`, `rating_star`) VALUES ('Item not available at store', '1', '5'), ('Item price is different in store', '1', '5'), ('Customer not reachable', '1', '0'), ('Customer denied to accept the items', '1', '0'), ('Customer location address not found', '1', '0'), ('Others', '1', '1');
/* If data available in reason details then update */
UPDATE reason_details SET cancel_reason = 'Item not available at store', rating_star = 5 WHERE id = 1;
UPDATE reason_details SET cancel_reason = 'Item price is different in store', rating_star = 5 WHERE id =2;
UPDATE reason_details SET cancel_reason = 'Customer not reachable', rating_star = 0 WHERE id =3;
UPDATE reason_details SET cancel_reason = 'Customer denied to accept the items', rating_star = 0 WHERE id =4;
UPDATE reason_details SET cancel_reason = 'Customer location address not found', rating_star = 0 WHERE id =5;
UPDATE reason_details SET cancel_reason = 'Others', rating_star = 1 WHERE id =6;

INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('DBOY_GRESS_TIME', '10');
INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('DBOY_DEFAULT_RATING', '3');
INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('CUSTOMER_DEFAULT_RATING', '3');

/* ########### 2015.03.03 #################### */
DELETE FROM `delivr_db`.`preferences` WHERE `preferences`.`pref_key` = 'CUSTOMER_REWARD_AMOUNT';

INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('REFERRAL_REWARD_AMOUNT', '200');
INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('REFEREE_REWARD_AMOUNT', '200');
INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('NORMAL_USER_BONUS_AMOUNT', '200');


/*##########################2015-03-09###########################*/
INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('DEDUCTION_PERCENT', '10');
INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('PROFIT_CHECK_FLAG', '0');


/*##########################2015-03-11###########################*/
INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('LOCATION_UPDATE_TIMEOUT_IN_MIN', '10');

INSERT INTO `preferences` (`pref_key`, `value`) VALUES ('ENABLE_FREE_REGISTER', FALSE );

ALTER TABLE `delivery_boys`
CHANGE COLUMN `advance_amount` `advance_amount` DECIMAL(16,2) NULL DEFAULT NULL ,
CHANGE COLUMN `available_amount` `available_amount` DECIMAL(16,2) NULL DEFAULT NULL ,
CHANGE COLUMN `bank_amount` `bank_amount` DECIMAL(16,2) NULL DEFAULT NULL ,
CHANGE COLUMN `previous_due` `previous_due` DECIMAL(16,2) NULL DEFAULT NULL ,
CHANGE COLUMN `total_earnings` `total_earnings` DECIMAL(16,2) NULL DEFAULT NULL ,
CHANGE COLUMN `wallet_amount` `wallet_amount` DECIMAL(16,2) NULL DEFAULT NULL ;

ALTER TABLE `merchants`
CHANGE COLUMN `commission_percentage` `commission_percentage` DECIMAL(6,2) NULL DEFAULT NULL ,
CHANGE COLUMN `service_fee` `service_fee` DECIMAL(16,2) NULL DEFAULT NULL ;

/*===================== Environment Variable for SMS 2015-03-16 =======================*/
-DDELIVR_SMS_TOKEN=2L9pXlF2vCSXE4MwNHVP
-DDELIVR_SMS_FROM=Demo


/*2015-03-18*/
ALTER TABLE dboy_order_history DROP COLUMN dboy_paid;

/*2015/3/26*/
INSERT INTO preferences_types (group_name) VALUE ("General Setting"), ("Algorithm Setting");
INSERT INTO preferences_sections (section, group_id) VALUES ("Currency & Tax", 1),("Reward Configuration", 1),("Rating Configuration", 1),("Support Configuration", 1),("Company Information", 1),("Version Configuration", 1),("Default Image", 1);
INSERT INTO preferences_sections (section, group_id) VALUES ("Distance Selection", 2),("Time, Distance and Charge Configuration", 2),("Order Processing Configuration", 2),("Profit and Commission Configuration", 2);

/*4/1/2015*/
update dboy_advance_amounts set type = "advanceAmount";
INSERT INTO dboy_advance_amounts (advance_date, advance_amount, dboy_id, type) SELECT ack_date, amount_received, dboy_id, "acknowledgeAmount" FROM dboy_submitted_amounts;
DROP TABLE dboy_submitted_amounts;
/*remember the */


/* old orders are from Cash on delivery 2015-04-03*/
UPDATE orders SET payment_mode = 0;

/*Transient field in database deleted 2015-04-08*/
ALTER TABLE `delivr_db`.`ratings` DROP COLUMN `orderId`;

/* ========= 2015-04-09 ===================== */
INSERT INTO `preferences` (`id`, `pref_key`, `value`, `section_id`, `pref_title`) VALUES (NULL, 'DEFAULT_IMG_SEARCH', 'https://delivrtest.s3.amazonaws.com/default/search/def_search.jpg', '7', 'Default Search Image');


/* ========= delete unused fields from orders table 2015-04-11 ===================== */
ALTER TABLE orders DROP COLUMN transportation_charge;
ALTER TABLE orders DROP COLUMN delivery_boy_share;
ALTER TABLE orders DROP COLUMN system_share;

/*wallet configuration=====2015-04-21*/
INSERT INTO `preferences_sections` (`section`, `group_id`) VALUES ('Wallet Configuration', '1');
INSERT INTO `delivr_db`.`preferences` (`pref_key`, `value`, `section_id`, `pref_title`) VALUES ('MINIMUM_TRANSFERABLE_AMOUNT', '100', '12', 'Minimum Transferable Amount');
INSERT INTO `delivr_db`.`preferences` (`pref_key`, `value`, `section_id`, `pref_title`) VALUES ('INR_CONVERSION_RATE', '1.6', '12', 'Indian Currency Conversion Rate');

/*set default value of customer custom flaf at custom item ========4-23-2015*/
ALTER TABLE delivr_db.custom_items CHANGE COLUMN customer_custom customer_custom BOOLEAN NOT NULL DEFAULT FALSE;

/*======================2015-04-23=========================*/
INSERT INTO `delivr_db`.`preferences` (`pref_key`, `value`, `section_id`, `pref_title`) VALUES ('TRANSFER_BONUS_PERCENT', '5', '12', 'Fund Transfer Bonus %');

/*======================make oneToOne foreign keys unique===================*/
ALTER TABLE bills ADD UNIQUE (order_id);
ALTER TABLE receipts ADD UNIQUE (order_id);
ALTER TABLE cart_custom_items ADD UNIQUE (cart_id);
ALTER TABLE ratings ADD UNIQUE (order_id);
ALTER TABLE courier_transaction ADD UNIQUE (order_id);
ALTER TABLE customers ADD UNIQUE (user_id);
ALTER TABLE delivery_boys ADD UNIQUE (user_id);
ALTER TABLE merchants ADD UNIQUE (user_id);
ALTER TABLE custom_items ADD UNIQUE(items_order_id);
ALTER TABLE order_cancel ADD UNIQUE (order_id);
ALTER TABLE user_device ADD UNIQUE (user_id);

/*======================2015-05-06=========================*/
INSERT INTO `delivr_db`.`preferences` (`pref_key`, `value`, `section_id`, `pref_title`) VALUES ('TDS_PERCENTAGE', '15', '1', 'TDS Percentage');
ALTER TABLE invoices DROP COLUMN paid_amount;

/*======================2015-05-11=========================*/
UPDATE `delivr_db`.`preferences` SET `section_id` = '4' WHERE `preferences`.`pref_key` = "ENABLE_FREE_REGISTER";

/*======================2015-05-06=========================*/
INSERT INTO `delivr_db`.`preferences` (`pref_key`, `value`, `section_id`, `pref_title`) VALUES ('REFERRAL_FACEBOOK_IMG', '', '7', 'Facebook Referral Image');
INSERT INTO `delivr_db`.`preferences` (`pref_key`, `value`, `section_id`, `pref_title`) VALUES ('APPLICATION_NAME', 'Koolkat', '5', 'Application Name');

/*======================2015-05-19=========================*/
ALTER TABLE customers MODIFY COLUMN is_default TINYINT(1) NOT NULL DEFAULT 0;

/*=========================2015-05-25======================*/
INSERT INTO `delivr_db`.`preferences` (`pref_key`, `value`, `section_id`, `pref_title`) VALUES ('DISCOUNT_ON_DELIVERY_FEE', '100', '11', 'Discount on delivery fee %');

/* =============================2015-05-27======================================= */
INSERT INTO `delivr_db`.`preferences` (`pref_key`, `value`, `section_id`, `pref_title`) VALUES ('IOS_APP_VER_NO', '1.0', '6', 'IOS Application Version No');

/* =============================2015-05-28======================================= */
INSERT INTO `delivr_db`.`preferences` (`pref_key`, `value`, `section_id`, `pref_title`) VALUES ('MAX_ORDER_SERVING_DISTANCE', '30', '9', 'Maximum order serving distance in km');
INSERT INTO `delivr_db`.`preferences` (`pref_key`, `value`, `section_id`, `pref_title`) VALUES ('LOGO_FOR_PDF_EMAIL', 'https://d1te5b0wbrygh3.cloudfront.net/default/login_logo/preference_661432553590098.png', '7', 'Logo For PDF and Email');

/*===2015-06-04==*/
UPDATE `delivr_db`.`orders` SET `discount_from_store` = 0 WHERE `discount_from_store` IS NULL;
/*Use just in test servers or local servers, but not in live*/
/*UPDATE `delivr_db`.`stores` SET `stores`.`send_email` = 0 WHERE `stores`.`send_email` = 1;*/


/*UTF-8 patch*/
ALTER TABLE `delivr_db`.`users`
CHANGE COLUMN `full_name` `full_name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_general_ci' NULL DEFAULT NULL ;

ALTER TABLE `delivr_db`.`address`
CHANGE COLUMN `full_name` `full_name` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `notes` `notes` LONGTEXT CHARACTER SET 'utf8' COLLATE 'utf8_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `street` `street` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_general_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `given_location` `given_location` VARCHAR(255) CHARACTER SET 'utf8' COLLATE 'utf8_general_ci' NULL DEFAULT NULL ;
