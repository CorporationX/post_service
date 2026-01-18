CREATE INDEX post_feed_idx
    ON post (author_id, created_at DESC, id DESC)
    WHERE published = true AND deleted = false;