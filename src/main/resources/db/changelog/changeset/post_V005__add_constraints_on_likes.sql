ALTER TABLE likes
ADD CONSTRAINT unique_user_post_like UNIQUE (user_id, post_id);
ALTER TABLE likes
ADD CONSTRAINT unique_user_comment_like UNIQUE (user_id, comment_id);