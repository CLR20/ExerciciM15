CREATE DATABASE luckyseven;

USE luckyseven;

CREATE TABLE users (
  id bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  registered varchar(255) DEFAULT NULL,
  rounds_played int NOT NULL,
  success_rate double NOT NULL,
  won_rounds int NOT NULL,
  PRIMARY KEY (id));

CREATE TABLE rounds (
  id bigint NOT NULL AUTO_INCREMENT,
  date varchar(255) DEFAULT NULL,
  first_dice int NOT NULL,
  result int NOT NULL,
  second_dice int NOT NULL,
  won varchar(255) DEFAULT NULL,
  user_id bigint DEFAULT NULL,
  PRIMARY KEY (id));