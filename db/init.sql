CREATE DATABASE IF NOT EXISTS login_db;
USE login_db;

CREATE TABLE IF NOT EXISTS Backup_users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50),
  password VARCHAR(50),
  email VARCHAR(100)
);

INSERT INTO Backup_users (username, password, email)
VALUES ('admin', 'Admin1234', 'admin@example.com');
