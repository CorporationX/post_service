ALTER TABLE post
    ADD COLUMN IF NOT EXISTS is_corrected boolean DEFAULT false;