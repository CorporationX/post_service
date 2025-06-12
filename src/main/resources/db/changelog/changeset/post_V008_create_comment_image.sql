--liquibase formatted sql

--changeset trytofixme:create_comment_image_20250612
CREATE TABLE comment_image (
    id          BIGSERIAL PRIMARY KEY,
    comment_id  BIGINT     NOT NULL,
    image_id    BIGINT     NOT NULL,
    preview_id  BIGINT     NOT NULL,
    created_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comment          FOREIGN KEY (comment_id) REFERENCES comment(id),
    CONSTRAINT fk_image_resource   FOREIGN KEY (image_id)   REFERENCES resource(id),
    CONSTRAINT fk_preview_resource FOREIGN KEY (preview_id) REFERENCES resource(id)
);
