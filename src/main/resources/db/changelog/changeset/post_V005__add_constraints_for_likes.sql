ALTER TABLE likes DROP CONSTRAINT IF EXISTS unique_user_post_like;
ALTER TABLE likes DROP CONSTRAINT IF EXISTS unique_user_comment_like;
ALTER TABLE likes DROP CONSTRAINT IF EXISTS chk_single_like_target;

ALTER TABLE likes
ADD CONSTRAINT unique_user_post_like UNIQUE (user_id, post_id);
ALTER TABLE likes
ADD CONSTRAINT unique_user_comment_like UNIQUE (user_id, comment_id);

ALTER TABLE likes
ADD CONSTRAINT chk_single_like_target
CHECK (
  (post_id IS NOT NULL AND comment_id IS NULL) OR
  (post_id IS NULL AND comment_id IS NOT NULL)
);