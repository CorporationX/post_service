ALTER TABLE post
ADD COLUMN hashtags JSONB;

CREATE INDEX idx_post_hashtags ON post USING GIN (hashtags jsonb_path_ops);