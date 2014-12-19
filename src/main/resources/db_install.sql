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
('DBOY_PER_KM_CHARGE_UPTO_2KM','50'),
('DBOY_PER_KM_CHARGE_ABOVE_2KM','30'),
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
('WEB_APP_VER_NO','0.1');

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