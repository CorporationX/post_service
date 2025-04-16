ALTER TABLE comment
    ADD COLUMN verified boolean,
    ADD COLUMN verified_at timestamptz;