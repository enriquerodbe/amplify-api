# --- !Ups

CREATE TABLE "users" (
  "id" BIGINT IDENTITY NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "auth_provider" VARCHAR(255) NOT NULL,
  "auth_identifier" VARCHAR(255) NOT NULL,
  UNIQUE KEY "users_auth_provider_identifier" ("auth_provider", "auth_identifier"));

CREATE TABLE "venues" (
  "id" BIGINT IDENTITY NOT NULL,
  "name" VARCHAR(255) NOT NULL,
  "uid" CHAR(8) NOT NULL,
  "auth_provider" VARCHAR(255) NOT NULL,
  "auth_identifier" VARCHAR(255) NOT NULL,
  "fcm_token" VARCHAR(255),
  UNIQUE KEY "venues_auth_provider_identifier" ("auth_provider", "auth_identifier"));

CREATE TABLE "queue_commands" (
  "id" BIGINT IDENTITY NOT NULL,
  "venue_id" BIGINT NOT NULL,
  "user_id" BIGINT,
  "queue_command_type" TINYINT NOT NULL,
  "content_provider" TINYINT,
  "content_identifier" VARCHAR(255),
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE "queue_events" (
  "id" BIGINT IDENTITY NOT NULL,
  "queue_command_id" BIGINT NOT NULL,
  "queue_event_type" TINYINT NOT NULL,
  "content_provider" TINYINT,
  "content_identifier" VARCHAR(255),
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);


# --- !Downs

DROP TABLE "users" IF EXISTS;
DROP TABLE "venues" IF EXISTS;
DROP TABLE "queue_commands" IF EXISTS;
DROP TABLE "queue_events" IF EXISTS;
