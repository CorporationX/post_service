CREATE TABLE resource
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    "key"      VARCHAR(50) NOT NULL,
    size       BIGINT,
    created_at timestamptz DEFAULT current_timestamp,
    name       VARCHAR(150),
    type       VARCHAR(50),
    post_id    BIGINT      NOT NULL,
    CONSTRAINT fk_post
        FOREIGN KEY (post_id) REFERENCES post (id)
            ON DELETE CASCADE
);