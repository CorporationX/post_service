ALTER TABLE album ADD COLUMN visibility smallint NOT NULL;
ALTER TABLE album ADD COLUMN allowed_users json;
