--liquibase formatted sql

--changeset trytofixme:rename_post_resource_to_resource_20250612
ALTER TABLE post_resource
RENAME TO resource;

--changeset trytofixme:alter_comment_images_20250630
ALTER TABLE comment
    ADD COLUMN large_image_resource_id BIGINT,
    ADD COLUMN small_image_resource_id BIGINT,
    ADD CONSTRAINT fk_comment_large_resource
        FOREIGN KEY (large_image_resource_id) REFERENCES resource(id),
    ADD CONSTRAINT fk_comment_small_resource
        FOREIGN KEY (small_image_resource_id) REFERENCES resource(id);