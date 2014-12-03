INSERT INTO roles (role) VALUES('ROLE_ADMIN'),('ROLE_MANAGER'), ('ROLE_ACCOUNTANT'),('ROLE_DELIVERY_BOY'),('ROLE_MERCHANT'),('ROLE_CUSTOMER');
INSERT INTO users (role_id, username, password, full_name, email, mobile_number, profile_image, street, city, state, country, country_code, mobile_verification_status, verification_status, subscribe_newsletter, black_list_status) VALUES (1, 'admin@yetistep.com', '$2a$10$dyij7cYmd27JZ0zN.OZeKuOvLU94jHHDrd7gAHBZWuvwKrZBqCVg2', 'Delivr Admin', 'admin@yetistep.com', '9849540028', '', 'Dillibazar', 'Kathmandu', 'Bagmati', 'Nepal', '977', true, true, true, false);
INSERT INTO countries(country_code, country_name, currency_code, currency_name) VALUES ('61', 'Australia', 'AUD', 'Australian Dollar'),('49', 'Germany', 'EUR', 'Euro'),('91', 'India', 'INR', 'Indian Rupee'),('977', 'Nepal', 'NPR', 'Nepalese Rupee'),('1', 'United States of America', 'USD', 'US Dollar');

