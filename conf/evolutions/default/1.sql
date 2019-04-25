# --- !Ups

CREATE TABLE "venues" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" VARCHAR(255) NOT NULL,
  "uid" CHAR(8) NOT NULL UNIQUE,
  "auth_provider" SMALLINT NOT NULL,
  "auth_identifier" VARCHAR(255) NOT NULL,
  "refresh_token" VARCHAR(255) NOT NULL,
  "access_token" VARCHAR(255) NOT NULL,
  UNIQUE ("auth_provider", "auth_identifier")
);

CREATE TABLE "queue_events" (
  "id" BIGSERIAL PRIMARY KEY,
  "venue_uid" CHAR(8) NOT NULL,
  "event_type" VARCHAR(63) NOT NULL,
  "coin_code" VARCHAR(255),
  "content_identifier" VARCHAR(255),
  "created_at" TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX index_on_queue_events_venue_uid ON "queue_events" ("venue_uid");

CREATE TABLE "coins" (
  "id" BIGSERIAL PRIMARY KEY,
  "venue_uid" CHAR(8) NOT NULL,
  "code" VARCHAR(255) UNIQUE,
  "max_usages" SMALLINT,
  "created_at" TIMESTAMP NOT NULL DEFAULT NOW(),
  UNIQUE ("venue_uid", "code")
);


# --- !Downs

DROP TABLE "venues";
DROP TABLE "queue_events";
DROP TABLE "coins";
