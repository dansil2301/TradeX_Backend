CREATE TABLE traders (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     username VARCHAR(255) NOT NULL UNIQUE,
     email VARCHAR(255) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL,
     status VARCHAR(255) NOT NULL,
     registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
