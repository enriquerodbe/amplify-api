# --- !Ups
CREATE TABLE "users" (
  "id" BIGINT IDENTITY NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "email" VARCHAR(255) NOT NULL UNIQUE,
  "auth_provider" VARCHAR(255) NOT NULL,
  "auth_identifier" VARCHAR(255) NOT NULL,
  UNIQUE KEY "auth_provider_identifier" ("auth_provider", "auth_identifier"));

CREATE TABLE "venues" (
  "id" BIGINT IDENTITY NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "user_id" BIGINT NOT NULL);

# --- !Downs
DROP TABLE "users" IF EXISTS;
DROP TABLE "venues" IF EXISTS;
