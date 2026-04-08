CREATE DATABASE IF NOT EXISTS gym_management
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE gym_management;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(128) NULL,
  role VARCHAR(32) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS gyms (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  location VARCHAR(255) NULL,
  description VARCHAR(1000) NULL,
  price DOUBLE NULL,
  image_url VARCHAR(512) NULL,
  PRIMARY KEY (id),
  KEY idx_gyms_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  gym_id BIGINT NOT NULL,
  booking_time DATETIME(6) NULL,
  status VARCHAR(32) NULL,
  PRIMARY KEY (id),
  KEY idx_bookings_user_id (user_id),
  KEY idx_bookings_gym_id (gym_id),
  CONSTRAINT fk_bookings_user_id FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_bookings_gym_id FOREIGN KEY (gym_id) REFERENCES gyms (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS ratings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  gym_id BIGINT NOT NULL,
  score INT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_ratings_user_gym (user_id, gym_id),
  KEY idx_ratings_user_id (user_id),
  KEY idx_ratings_gym_id (gym_id),
  CONSTRAINT fk_ratings_user_id FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_ratings_gym_id FOREIGN KEY (gym_id) REFERENCES gyms (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
