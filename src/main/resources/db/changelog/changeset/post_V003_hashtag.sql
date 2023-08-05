CREATE TABLE hashtags (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hashtag character varying(140) NOT NULL UNIQUE CHECK (hashtag ~ '^#[A-Za-z0-9]+$')
);

CREATE TABLE post_hashtag (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hashtag_id BIGINT REFERENCES hashtags(id),
    post_id BIGINT REFERENCES posts(id),

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT fk_hashtag_id FOREIGN KEY (hashtag_id) REFERENCES hashtag (id) ON DELETE CASCADE
);