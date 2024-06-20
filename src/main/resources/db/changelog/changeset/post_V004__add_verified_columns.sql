ALTER TABLE comment
    ADD COLUMN verified_date timestamptz,
    ADD COLUMN verified bool DEFAULT false NOT NULL;