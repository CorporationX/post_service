create TABLE hashtag
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    content    varchar(128) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

create TABLE post_hashtag (
                              post_id    bigint,
                              hashtag_id bigint
)