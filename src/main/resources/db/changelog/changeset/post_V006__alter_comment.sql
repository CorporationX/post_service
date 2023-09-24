ALTER TABLE comment
ADD COLUMN verified boolean,
ADD COLUMN verified_date timestamptz DEFAULT current_timestamp;