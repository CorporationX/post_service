ALTER TABLE album ADD COLUMN visibility smallint NOT NULL DEFAULT 0;
-- ALTER TABLE album ADD COLUMN allowed_users_ids JSON;
ALTER TABLE album ADD COLUMN allowed_users_ids VARCHAR;
