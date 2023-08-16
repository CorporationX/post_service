CREATE TABLE hashtag (
    id  bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tag varchar(255) UNIQUE
);
CREATE TABLE post_hashtag (
    post_id    bigint REFERENCES post (id),
    hashtag_id bigint REFERENCES hashtag (id),
    PRIMARY KEY (post_id, hashtag_id)
);

