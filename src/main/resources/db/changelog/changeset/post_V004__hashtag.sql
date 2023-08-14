CREATE TABLE hashtags (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hashtag character varying(140) NOT NULL UNIQUE CHECK (hashtag ~ '^#[A-Za-z0-9]+$')
);

CREATE INDEX idx_hashtag ON hashtags (hashtag);

CREATE TABLE post_hashtag (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hashtag_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT fk_hashtag_id FOREIGN KEY (hashtag_id) REFERENCES hashtags (id) ON DELETE CASCADE
);