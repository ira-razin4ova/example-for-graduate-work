--liquibase formatted sql

--changeset razinkova:1
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    image VARCHAR(255)
);

--changeset razinkova:2
CREATE TABLE IF NOT EXISTS ad (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    description TEXT,
    image VARCHAR(255),
    author_id SERIAL,

     CONSTRAINT ad_user_id FOREIGN KEY (author_id) REFERENCES users (id)
);

--changeset razinkova:3
CREATE TABLE IF NOT EXISTS comment (
    id SERIAL PRIMARY KEY,
    text VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    author_id SERIAL,
    ad_id SERIAL,

     CONSTRAINT com_user_id FOREIGN KEY (author_id) REFERENCES users (id),
     CONSTRAINT com_ad_id FOREIGN KEY (ad_id) REFERENCES ad (id)
);