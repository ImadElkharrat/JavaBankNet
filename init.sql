CREATE DATABASE IF NOT EXISTS JavaBankNet;
USE JavaBankNet;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'CLIENT')),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
    account_number VARCHAR(20) PRIMARY KEY,
    user_id INT NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    type VARCHAR(20) NOT NULL CHECK (type IN ('COURANT', 'EPARGNE')),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    source_acc VARCHAR(20),
    dest_acc VARCHAR(20),
    amount DECIMAL(15, 2) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('VIREMENT', 'DEPOT', 'RETRAIT', 'TRANSFER')),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (source_acc) REFERENCES accounts(account_number),
    FOREIGN KEY (dest_acc) REFERENCES accounts(account_number)
);

INSERT INTO users (username, password_hash, role) VALUES ('admin', 'admin123', 'ADMIN');
INSERT INTO users (username, password_hash, role) VALUES ('client1', 'pass123', 'CLIENT');
INSERT INTO users (username, password_hash, role) VALUES ('client2', 'pass123', 'CLIENT');

INSERT INTO accounts (account_number, user_id, balance, type) VALUES ('FR76-CLIENT1', 2, 4000.00, 'COURANT');
INSERT INTO accounts (account_number, user_id, balance, type) VALUES ('FR76123456789', 2, 10000.00, 'COURANT');
INSERT INTO accounts (account_number, user_id, balance, type) VALUES ('FR76-CLIENT2', 3, 5000.00, 'COURANT');
INSERT INTO accounts (account_number, user_id, balance, type) VALUES ('FR76987654321', 3, 500.00, 'COURANT');
