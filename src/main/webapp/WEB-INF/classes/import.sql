INSERT INTO roles (role) VALUES('ROLE_ADMIN'),('ROLE_MANAGER'), ('ROLE_ACCOUNTANT'),('ROLE_DELIVERY_BOY'),('ROLE_MERCHANT'),('ROLE_CUSTOMER');

INSERT INTO users (id, role_id, username, password, full_name, email, mobile_number, profile_image, mobile_verification_status, verification_status, subscribe_newsletter, black_list_status) VALUES (1, 1, 'admin@yetistep.com', '$2a$10$dyij7cYmd27JZ0zN.OZeKuOvLU94jHHDrd7gAHBZWuvwKrZBqCVg2', 'Delivr Admin', 'admin@yetistep.com', '9849540028', '', true, 1, true, false);

INSERT INTO users (id, role_id, username, password, full_name, email, mobile_number, profile_image, mobile_verification_status, verification_status, subscribe_newsletter, black_list_status) VALUES (2, 2, 'manager@yetistep.com', '$2a$10$dyij7cYmd27JZ0zN.OZeKuOvLU94jHHDrd7gAHBZWuvwKrZBqCVg2', 'Delivr Manager', 'manager@yetistep.com', '9849540028', '', true, 1, true, false);

INSERT INTO users (id, role_id, username, password, full_name, email, mobile_number, profile_image, mobile_verification_status, verification_status, subscribe_newsletter, black_list_status) VALUES (3, 3, 'accountant@yetistep.com', '$2a$10$dyij7cYmd27JZ0zN.OZeKuOvLU94jHHDrd7gAHBZWuvwKrZBqCVg2', 'Delivr Accountant', 'accountant@yetistep.com', '9849540028', '', true, 1, true, false);

INSERT INTO address (street, city, state, country, country_code, user_id) VALUES ('Dillibazar', 'Kathmandu', 'Bagmati', 'Nepal', '977', 1);

INSERT INTO address (street, city, state, country, country_code, user_id) VALUES ('Dillibazar', 'Kathmandu', 'Bagmati', 'Nepal', '977', 2);

INSERT INTO address (street, city, state, country, country_code, user_id) VALUES ('Dillibazar', 'Kathmandu', 'Bagmati', 'Nepal', '977', 3);

INSERT INTO countries (country_code, country_name, currency_code, currency_name) VALUES ('61', 'Australia', 'AUD', 'Australian Dollar'),('49', 'Germany', 'EUR', 'Euro'),('91', 'India', 'INR', 'Indian Rupee'),('977', 'Nepal', 'NPR', 'Nepalese Rupee'),('1', 'United States of America', 'USD', 'US Dollar');

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Food & Beverages', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Beauty, Health & Personal Care', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Clothing, Shoes, Bags & Accessories', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Consumer Electronics', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Computers, Mobile Phone & Cameras', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Entertainment & Sports', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Toys & Gifts', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Gifts, Cake, Flowers & Party Decoration', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Fashion, Watches, Jewelry & Eye wear', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Books & Magazines', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Furniture,Lights, Garden & Tools', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Automotive & Accessories', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Restaurants', NULL, NULL, NULL);

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Soft Drinks', NULL, 1, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Alcoholic Beverages', NULL, 1, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Confectionery', NULL, 1, NULL);

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Bath Supplies', NULL, 2, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Beauty Equipment', NULL, 2, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Fragrance & Deodorant', NULL, 2, NULL);

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Clothing', NULL, 3, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Shoes', NULL, 3, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Bags', NULL, 3, NULL);

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Televisions', NULL, 4, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Air Conditioners', NULL, 4, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Vacuum Cleaners', NULL, 4, NULL);

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Tablet PCs', NULL, 5, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Laptops', NULL, 5, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Mobiles', NULL, 5, NULL);

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Outdoor Sports', NULL, 6, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Fitness & Body Building', NULL, 6, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Musical Instruments', NULL, 6, NULL);

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Action Figures', NULL, 7, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Baby Toys', NULL, 7, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Classic Toys', NULL, 7, NULL);

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Gifts & Crafts', NULL, 8, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Cakes', NULL, 8, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Flowers', NULL, 8, NULL);

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Hats & Caps', NULL, 9, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Hair Accessories', NULL, 9, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Scarves & Shawls', NULL, 9, NULL);

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, brand_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Books', NULL, 10, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Magazines', NULL, 10, NULL);

TRUNCATE TABLE preferences;
INSERT INTO preferences (pref_key,VALUE) VALUES('CURRENCY','$'),('DBOY_ADDITIONAL_PER_KM_CHARGE','5'),('DBOY_PER_KM_CHARGE_UPTO_2KM','50'),('DBOY_PER_KM_CHARGE_ABOVE_2KM','30'),('RESERVED_COMM_PER_BY_SYSTEM','50'),('SURGE_FACTOR_7AM_9PM','1'),('SURGE_FACTOR_9_10PM','2'),('SURGE_FACTOR_10_11PM','3'),('SURGE_FACTOR_11PM_7AM','4'),('DBOY_COMMISSION','80'),('DBOY_MIN_AMOUNT','50'),('MERCHANT_VAT','13'),('MERCHANT_SERVICE_CHARGE','10'),('ANDROID_APP_VER_NO','0.1'),('WEB_APP_VER_NO','0.1'),('TIME_TO_TRAVEL_ONE_KM_ON_FOOT', '15'),('TIME_TO_TRAVEL_ONE_KM_ON_BICYCLE', '6'),('TIME_TO_TRAVEL_ONE_KM_ON_MOTORBIKE', '5'),('TIME_TO_TRAVEL_ONE_KM_ON_CAR', '5'),('TIME_TO_TRAVEL_ONE_KM_ON_TRUCK', '10'),('TIME_TO_TRAVEL_ONE_KM_ON_OTHERS', '10'),('DEVIATION_IN_TIME','15'),('TIME_AT_STORE','30');



