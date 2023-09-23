ALTER TABLE comment
ADD COLUMN verified boolean,
ADD COLUMN verifiedDate timestamptz DEFAULT current_timestamp;