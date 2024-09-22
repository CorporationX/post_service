CREATE TABLE hashtag (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name varchar(64) NOT NULL
);

CREATE TABLE posts_hashtags(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    post_id bigint NOT NULL REFERENCES post(id),
    hashtag_id bigint NOT NULL REFERENCES hashtag(id)
);