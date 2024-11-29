ALTER TABLE comment
    ADD COLUMN if not exists verified boolean,
    ADD COLUMN if not exists verified_at timestamp with time zone;