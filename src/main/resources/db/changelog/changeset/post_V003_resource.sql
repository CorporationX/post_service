CREATE TABLE IF NOT EXISTS post_resource
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    key        VARCHAR(255),
    type       VARCHAR(255),
    status     VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    post_id BIGINT,
    size       BIGINT,
    CONSTRAINT fk_post
    FOREIGN KEY (post_id) REFERENCES post (id)
    );