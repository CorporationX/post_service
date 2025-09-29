CREATE INDEX IF NOT EXISTS ix_comment_post_created_desc
    ON comment (post_id, created_at DESC);