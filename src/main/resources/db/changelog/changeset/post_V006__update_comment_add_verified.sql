ALTER TABLE comment
    ADD COLUMN IF NOT EXISTS verified boolean;

ALTER TABLE comment
    ADD COLUMN IF NOT EXISTS verified_date timestamptz;