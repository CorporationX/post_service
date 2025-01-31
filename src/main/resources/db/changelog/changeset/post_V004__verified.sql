ALTER TABLE post
    ADD COLUMN if not exists verified boolean DEFAULT FALSE NOT NULL