CREATE TABLE IF NOT EXISTS hashtag
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS post_hashtags
(
    post_id bigint NOT NULL,
    hashtag_id bigint NOT NULL,
    PRIMARY KEY (post_id, hashtag_id),
    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT fk_hashtag_id FOREIGN KEY (hashtag_id) REFERENCES hashtag (id) ON DELETE CASCADE
)