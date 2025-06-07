--liquibase formatted sql

--changeset trytofixme:create_comment_image
CREATE TABLE comment_image (
    id SERIAL PRIMARY KEY,
    user_id BIGINT,
    file_key TEXT,
    preview_key TEXT,
    content_type TEXT,
    size BIGINT
);
