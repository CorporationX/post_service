ALTER TABLE post
ADD COLUMN verified_date timestamptz,
ADD COLUMN verified BOOLEAN DEFAULT false NOT NULL;

