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
  UNIQUE ("auth_provider", "auth_identifier"));

CREATE TABLE "venues_journal" (
  "ordering" BIGSERIAL,
  "persistence_id" VARCHAR(255) NOT NULL,
  "sequence_number" BIGINT NOT NULL,
  "deleted" BOOLEAN DEFAULT FALSE,
  "tags" VARCHAR(255) DEFAULT NULL,
  "message" BYTEA NOT NULL,
  "time" TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (persistence_id, sequence_number)
);

CREATE UNIQUE INDEX "venues_journal_ordering_idx" ON "venues_journal" ("ordering");

CREATE TABLE "venues_snapshot" (
  "persistence_id" VARCHAR(255) NOT NULL,
  "sequence_number" BIGINT NOT NULL,
  "created" BIGINT NOT NULL,
  "snapshot" BYTEA NOT NULL,
  "time" TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (persistence_id, sequence_number)
);

CREATE TABLE "coins" (
  "id" BIGSERIAL PRIMARY KEY,
  "venue_id" BIGINT NOT NULL,
  "token" VARCHAR(255) UNIQUE,
  "max_usages" SMALLINT,
  "time" TIMESTAMP NOT NULL DEFAULT NOW()
);


# --- !Downs

DROP TABLE "users";
DROP TABLE "venues";
DROP TABLE "venues_journal";
DROP TABLE "venues_snapshot";
DROP TABLE "coins";
