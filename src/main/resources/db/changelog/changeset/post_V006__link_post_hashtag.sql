CREATE TABLE post_hashtag (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    post_id bigint NOT NULL,
    hashtag_id bigint NOT NULL
);