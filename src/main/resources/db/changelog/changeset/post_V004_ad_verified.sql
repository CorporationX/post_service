ALTER TABLE post
    ADD COLUMN IF NOT EXISTS verified boolean DEFAULT false NOT NULL,
    ADD COLUMN IF NOT EXISTS verified_date timestamptz;