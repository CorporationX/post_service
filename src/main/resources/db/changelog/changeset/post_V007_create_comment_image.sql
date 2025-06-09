--liquibase formatted sql

--changeset trytofixme:create_comment_image1
drop table if exists comment_image;

CREATE TABLE comment_image (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    file_key TEXT NOT NULL,
    preview_key TEXT NOT NULL,
    content_type TEXT NOT NULL,
    size BIGINT
);
