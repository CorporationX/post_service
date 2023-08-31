CREATE TABLE hashtag
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    content    varchar(128) NOT NULL,
    post_id    bigint,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

ALTER TABLE post
    ADD hashtag_id bigint;