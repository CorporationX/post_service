ALTER TABLE comment
  ADD COLUMN verified BOOLEAN DEFAULT FALSE;

ALTER TABLE comment
  ADD COLUMN in_progress BOOLEAN DEFAULT FALSE;

CREATE INDEX idx_comments_verified_in_progress ON comment (verified, in_progress);