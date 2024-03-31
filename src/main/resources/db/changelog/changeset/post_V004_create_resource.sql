CREATE TABLE IF NOT EXISTS resource
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    key        VARCHAR(255),
    type       VARCHAR(255),
    status     VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    project_id BIGINT,
    size       BIGINT,
    post_id BIGINT,
    CONSTRAINT fk_project
    FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_post
    FOREIGN KEY (post_id) REFERENCES post (id)
    );