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

INSERT INTO preferences (pref_key, value) VALUES (NULL, 'ORDER_REQUEST_TIMEOUT_IN_MIN', '4');
DELETE FROM preferences WHERE pref_key IN ('MERCHANT_VAT', 'MERCHANT_SERVICE_CHARGE');
