ALTER TABLE comment
ADD COLUMN verified_date timestamptz,
ADD COLUMN verified boolean;