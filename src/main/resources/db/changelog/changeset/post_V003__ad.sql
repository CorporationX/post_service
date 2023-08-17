ALTER TABLE post
    ADD COLUMN verified BOOLEAN DEFAULT FALSE,
    ADD COLUMN verified_date TIMESTAMPTZ DEFAULT current_timestamp;
