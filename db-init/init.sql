-- Local dev only. This password must match DB_PASSWORD in your .env file.
CREATE USER IF NOT EXISTS 'ticketing_app'@'%' IDENTIFIED BY 'ticketing_app_pw';
GRANT ALL PRIVILEGES ON data_normalization.* TO 'ticketing_app'@'%';
FLUSH PRIVILEGES;