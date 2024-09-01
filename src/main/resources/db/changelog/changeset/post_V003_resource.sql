CREATE TABLE resource
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    "key"      VARCHAR(255) NOT NULL,
    name       VARCHAR(255),
    type       VARCHAR(255),
    size       BIGINT,
    created_at timestamptz DEFAULT current_timestamp,
    post_id    BIGINT      NOT NULL,
    CONSTRAINT fk_post
        FOREIGN KEY (post_id) REFERENCES post (id)
            ON DELETE CASCADE
);