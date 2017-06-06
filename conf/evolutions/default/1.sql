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
  "user_id" BIGINT NOT NULL,
  "uid" CHAR(8) NOT NULL,
  UNIQUE KEY "unique_user_id" ("user_id"));

CREATE TABLE "event_sources" (
  "id" BIGINT IDENTITY NOT NULL,
  "venue_id" BIGINT NOT NULL,
  "user_id" BIGINT,
  "event_type" TINYINT NOT NULL,
  "content_provider" TINYINT,
  "content_identifier" VARCHAR(255),
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE "queue_events" (
  "id" BIGINT IDENTITY NOT NULL,
  "event_source_id" BIGINT NOT NULL,
  "event_type" TINYINT NOT NULL,
  "content_provider" TINYINT,
  "content_identifier" VARCHAR(255),
  "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);


# --- !Downs

DROP TABLE "users" IF EXISTS;
DROP TABLE "venues" IF EXISTS;
DROP TABLE "event_sources" IF EXISTS;
DROP TABLE "queue_events" IF EXISTS;
