# --- !Ups

CREATE TABLE "users" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" VARCHAR(255) NOT NULL,
  "auth_provider" SMALLINT NOT NULL,
  "auth_identifier" VARCHAR(255) NOT NULL,
  UNIQUE ("auth_provider", "auth_identifier"));

CREATE TABLE "venues" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" VARCHAR(255) NOT NULL,
  "uid" CHAR(8) NOT NULL UNIQUE,
  "auth_provider" SMALLINT NOT NULL,
  "auth_identifier" VARCHAR(255) NOT NULL,
  "fcm_token" VARCHAR(255),
  UNIQUE ("auth_provider", "auth_identifier"));

CREATE TABLE "queue_commands" (
  "id" BIGSERIAL PRIMARY KEY,
  "venue_id" BIGINT NOT NULL,
  "data" TEXT NOT NULL,
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE "queue_events" (
  "id" BIGSERIAL PRIMARY KEY,
  "queue_command_id" BIGINT NOT NULL,
  "data" TEXT NOT NULL,
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);


# --- !Downs

DROP TABLE IF EXISTS "users";
DROP TABLE IF EXISTS "venues";
DROP TABLE IF EXISTS "queue_commands";
DROP TABLE IF EXISTS "queue_events";
