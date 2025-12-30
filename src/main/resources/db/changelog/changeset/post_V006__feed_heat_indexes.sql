CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_post_author_id_not_null
ON post(author_id) WHERE author_id IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_likes_user_id_not_null 
ON likes(user_id) WHERE user_id IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_comment_author_id_not_null 
ON comment(author_id) WHERE author_id IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_post_published_lookup 
ON post(author_id, published_at DESC) 
WHERE published = true AND deleted = false AND published_at IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_comment_post_id 
ON comment(post_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_like_post_id 
ON likes(post_id);

