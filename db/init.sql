CREATE DATABASE IF NOT EXISTS login_db;
USE login_db;

-- Create table
CREATE TABLE IF NOT EXISTS Backup_users (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(25),
    password VARCHAR(25),
    email VARCHAR(25),
    PRIMARY KEY (id)
);

INSERT INTO Backup_users (username, password, email) VALUES
('admin', 'Admin1234', 'admin@example.com');
