CREATE DATABASE IF NOT EXISTS JavaBankNet;
USE JavaBankNet;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
    account_number VARCHAR(20) PRIMARY KEY,
    user_id INT NOT NULL,
    balance DOUBLE DEFAULT 0.0,
    type VARCHAR(20) DEFAULT 'COURANT',
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sourceAccountId VARCHAR(20),
    destAccountId VARCHAR(20),
    amount DOUBLE NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sourceAccountId) REFERENCES accounts(account_number),
    FOREIGN KEY (destAccountId) REFERENCES accounts(account_number)
);

-- Insert sample data
INSERT INTO users (username, password_hash, role) VALUES ('admin', 'admin123', 'ADMIN');
INSERT INTO users (username, password_hash, role) VALUES ('client1', 'pass123', 'CLIENT');
INSERT INTO users (username, password_hash, role) VALUES ('client2', 'pass123', 'CLIENT');

-- Sample accounts
INSERT INTO accounts (account_number, user_id, balance, type) VALUES ('FR76123456789', 2, 1000.50, 'COURANT');
INSERT INTO accounts (account_number, user_id, balance, type) VALUES ('FR76987654321', 3, 500.00, 'COURANT');
