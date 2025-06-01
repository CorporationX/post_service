ALTER TABLE post ADD COLUMN IF NOT EXISTS views bigint default 0 NOT NULL;

CREATE INDEX IF NOT EXISTS idx_post_author_date ON post(author_id, published_at DESC);
