CREATE TYPE verification_status AS ENUM ('UNVERIFIED', 'VERIFIED', 'REJECTED');

ALTER TABLE post
ADD COLUMN verification_status verification_status NOT NULL DEFAULT 'UNVERIFIED';

ALTER TABLE post
ADD COLUMN verified_date TIMESTAMPTZ;