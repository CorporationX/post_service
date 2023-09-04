ALTER TABLE post
    ADD COLUMN spell_check      BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN spell_checked_at timestamptz;