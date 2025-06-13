ALTER TABLE post
    ADD COLUMN if not exists verified boolean DEFAULT false NOT NULL;