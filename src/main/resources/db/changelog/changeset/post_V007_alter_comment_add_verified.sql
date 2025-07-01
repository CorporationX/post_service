ALTER TABLE comment
  ADD COLUMN verified BOOLEAN DEFAULT FALSE,
  ADD COLUMN verified_at TIMESTAMP;

  ADD COLUMN in_progress BOOLEAN NOT NULL DEFAULT FALSE;

  CREATE INDEX idx_comments_verified_in_progress ON comments (verified, in_progress);