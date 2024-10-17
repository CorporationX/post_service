ALTER TABLE post
    ADD COLUMN IF NOT EXISTS verified boolean,
    ADD COLUMN IF NOT EXISTS verified_date timestamptz;