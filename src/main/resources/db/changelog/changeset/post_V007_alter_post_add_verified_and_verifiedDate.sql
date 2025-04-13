ALTER TABLE post
ADD COLUMN verified boolean DEFAULT false NOT NULL,
ADD COLUMN verified_at TIMESTAMP;