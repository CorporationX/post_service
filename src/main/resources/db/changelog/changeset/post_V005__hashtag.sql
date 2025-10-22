CREATE TABLE hashtag (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    post_id BIGINT NOT NULL
);

CREATE INDEX idx_hashtag_name ON hashtag(name);