ALTER TABLE album ADD COLUMN visibility smallint NOT NULL DEFAULT 0;
-- ALTER TABLE album ADD COLUMN allowed_users_ids JSON;
ALTER TABLE album ADD COLUMN allowed_users_ids VARCHAR;

ALTER TABLE post
    ADD COLUMN verified BOOLEAN DEFAULT FALSE,
    ADD COLUMN verified_date TIMESTAMPTZ DEFAULT current_timestamp;
