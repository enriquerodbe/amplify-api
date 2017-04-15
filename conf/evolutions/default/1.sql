# --- !Ups
CREATE TABLE "users" (
  "id" BIGINT IDENTITY NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "email" VARCHAR(255) NOT NULL UNIQUE,
  "auth_identifier" VARCHAR(255) NOT NULL,
  "auth_provider" VARCHAR(255) NOT NULL);

CREATE TABLE "venues" (
  "id" BIGINT IDENTITY NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "user_id" BIGINT NOT NULL);

# --- !Downs
DROP TABLE "users" IF EXISTS;
DROP TABLE "venues" IF EXISTS;
