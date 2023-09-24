ALTER TABLE comment DROP COLUMN verified,
ADD COLUMN verified boolean DEFAULT false;