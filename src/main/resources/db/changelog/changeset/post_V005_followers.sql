CREATE TABLE followers (
    author_id bigint NOT NULL,
    follower_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    PRIMARY KEY (author_id, follower_id)
);