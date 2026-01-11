create TABLE IF NOT EXISTS 'employee' (
    'id' BIGINT AUTO_INCREMENT PRIMARY KEY,
    'first_name' VARCHAR(255) NOT NULL,
    'last_name' VARCHAR(255) NOT NULL,
    'email' VARCHAR(255) NOT NULL UNIQUE,
    'version' BIGINT DEFAULT 0
);