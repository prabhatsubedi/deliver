INSERT INTO roles (role) VALUES('ROLE_ADMIN'),('ROLE_MANAGER'), ('ROLE_ACCOUNTANT'),('ROLE_DELIVERY_BOY'),('ROLE_MERCHANT'),('ROLE_CUSTOMER');

INSERT INTO users (id, role_id, username, password, full_name, email, mobile_number, profile_image, mobile_verification_status, verification_status, subscribe_newsletter, black_list_status) VALUES (1, 1, 'admin@yetistep.com', '$2a$10$dyij7cYmd27JZ0zN.OZeKuOvLU94jHHDrd7gAHBZWuvwKrZBqCVg2', 'Delivr Admin', 'admin@yetistep.com', '9849540028', '', true, 1, true, false);

INSERT INTO users (id, role_id, username, password, full_name, email, mobile_number, profile_image, mobile_verification_status, verification_status, subscribe_newsletter, black_list_status) VALUES (2, 2, 'manager@yetistep.com', '$2a$10$dyij7cYmd27JZ0zN.OZeKuOvLU94jHHDrd7gAHBZWuvwKrZBqCVg2', 'Delivr Manager', 'manager@yetistep.com', '9849540028', '', true, 1, true, false);

INSERT INTO users (id, role_id, username, password, full_name, email, mobile_number, profile_image, mobile_verification_status, verification_status, subscribe_newsletter, black_list_status) VALUES (3, 3, 'accountant@yetistep.com', '$2a$10$dyij7cYmd27JZ0zN.OZeKuOvLU94jHHDrd7gAHBZWuvwKrZBqCVg2', 'Delivr Accountant', 'accountant@yetistep.com', '9849540028', '', true, 1, true, false);

INSERT INTO address (street, city, state, country, country_code, user_id) VALUES ('Dillibazar', 'Kathmandu', 'Bagmati', 'Nepal', '977', 1);

INSERT INTO address (street, city, state, country, country_code, user_id) VALUES ('Dillibazar', 'Kathmandu', 'Bagmati', 'Nepal', '977', 2);

INSERT INTO address (street, city, state, country, country_code, user_id) VALUES ('Dillibazar', 'Kathmandu', 'Bagmati', 'Nepal', '977', 3);

INSERT INTO countries (country_code, country_name, currency_code, currency_name) VALUES ('61', 'Australia', 'AUD', 'Australian Dollar'),('49', 'Germany', 'EUR', 'Euro'),('91', 'India', 'INR', 'Indian Rupee'),('977', 'Nepal', 'NPR', 'Nepalese Rupee'),('1', 'United States of America', 'USD', 'US Dollar');

INSERT INTO categories (id, created_date, featured, name, priority, parent_id, store_id) VALUES (NULL, CURRENT_TIMESTAMP, '0', 'Food & Beverages', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Beauty, Health & Grocery', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Clothing, Shoes & Jewelry', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Toys, Kids & Baby', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Sports & Outdoors', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Gifts, Cakes & Flowers', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Movies, Music & Games', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Books & Magazines', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Consumer Electronics', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Computers, Mobile Phone & Cameras', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Fashion & Lifestyle', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Home, Garden & Tools', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Automotive & Accessories', NULL, NULL, NULL), (NULL, CURRENT_TIMESTAMP, '0', 'Restaurants', NULL, NULL, NULL);

DROP TABLE items_store;
DROP TABLE items_image;