INSERT INTO roles (role) VALUES('ROLE_ADMIN'),('ROLE_MANAGER'), ('ROLE_ACCOUNTANT'),('ROLE_DELIVERY_BOY'),('ROLE_MERCHANT'),('ROLE_CUSTOMER');

INSERT INTO users (id, role_id, username, password, full_name, email, mobile_number, profile_image, mobile_verification_status, verification_status, subscribe_newsletter, black_list_status) VALUES (1, 1, 'admin@yetistep.com', '$2a$10$dyij7cYmd27JZ0zN.OZeKuOvLU94jHHDrd7gAHBZWuvwKrZBqCVg2', 'Delivr Admin', 'admin@yetistep.com', '9849540028', '', true, 1, true, false);

INSERT INTO users (id, role_id, username, password, full_name, email, mobile_number, profile_image, mobile_verification_status, verification_status, subscribe_newsletter, black_list_status) VALUES (2, 2, 'manager@yetistep.com', '$2a$10$dyij7cYmd27JZ0zN.OZeKuOvLU94jHHDrd7gAHBZWuvwKrZBqCVg2', 'Delivr Manager', 'manager@yetistep.com', '9849540028', '', true, 1, true, false);

INSERT INTO users (id, role_id, username, password, full_name, email, mobile_number, profile_image, mobile_verification_status, verification_status, subscribe_newsletter, black_list_status) VALUES (3, 3, 'accountant@yetistep.com', '$2a$10$dyij7cYmd27JZ0zN.OZeKuOvLU94jHHDrd7gAHBZWuvwKrZBqCVg2', 'Delivr Accountant', 'accountant@yetistep.com', '9849540028', '', true, 1, true, false);

INSERT INTO address (street, city, state, country, country_code, user_id) VALUES ('Dillibazar', 'Kathmandu', 'Bagmati', 'Nepal', '977', 1);

INSERT INTO address (street, city, state, country, country_code, user_id) VALUES ('Dillibazar', 'Kathmandu', 'Bagmati', 'Nepal', '977', 2);

INSERT INTO address (street, city, state, country, country_code, user_id) VALUES ('Dillibazar', 'Kathmandu', 'Bagmati', 'Nepal', '977', 3);

INSERT INTO countries (country_code, country_name, currency_code, currency_name) VALUES ('61', 'Australia', 'AUD', 'Australian Dollar'),('49', 'Germany', 'EUR', 'Euro'),('91', 'India', 'INR', 'Indian Rupee'),('977', 'Nepal', 'NPR', 'Nepalese Rupee'),('1', 'United States of America', 'USD', 'US Dollar');

TRUNCATE TABLE preferences;
INSERT INTO preferences (pref_key,VALUE) VALUES('CURRENCY','$'),('DBOY_ADDITIONAL_PER_KM_CHARGE','5'),('DBOY_PER_KM_CHARGE_UPTO_2KM','50'),('DBOY_PER_KM_CHARGE_ABOVE_2KM','30'),('RESERVED_COMM_PER_BY_SYSTEM','50'),('SURGE_FACTOR_7AM_9PM','1'),('SURGE_FACTOR_9_10PM','2'),('SURGE_FACTOR_10_11PM','3'),('SURGE_FACTOR_11PM_7AM','4'),('DBOY_COMMISSION','80'),('DBOY_MIN_AMOUNT','50'),('ANDROID_APP_VER_NO','0.1'),('WEB_APP_VER_NO','0.1'),('TIME_TO_TRAVEL_ONE_KM_ON_FOOT', '15'),('TIME_TO_TRAVEL_ONE_KM_ON_BICYCLE', '6'),('TIME_TO_TRAVEL_ONE_KM_ON_MOTORBIKE', '5'),('TIME_TO_TRAVEL_ONE_KM_ON_CAR', '5'),('TIME_TO_TRAVEL_ONE_KM_ON_TRUCK', '10'),('TIME_TO_TRAVEL_ONE_KM_ON_OTHERS', '10'),('DEVIATION_IN_TIME','15'),('TIME_AT_STORE','30'),('CUSTOMER_REWARD_AMOUNT', '200'),('MAX_REFERRED_FRIENDS_COUNT', '3'), ('DELIVERY_FEE_VAT','0'), ('MINIMUM_PROFIT_PERCENTAGE','10'), ('ADDITIONAL_KM_FREE_LIMIT','1'), ('DEFAULT_NKM_DISTANCE', 4),('AIR_TO_ROUTE_DISTANCE_FACTOR','1.5'),('AIR_OR_ACTUAL_DISTANCE_SWITCH','1'),('ORDER_REQUEST_TIMEOUT_IN_MIN', 4);



