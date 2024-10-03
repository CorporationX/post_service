ALTER TABLE post
ALTER COLUMN verification_status TYPE VARCHAR(50)
      USING verification_status::text;