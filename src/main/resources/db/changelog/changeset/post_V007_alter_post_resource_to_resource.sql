--liquibase formatted sql

--changeset trytofixme:rename_post_resource_to_resource_20250612
ALTER TABLE post_resource
RENAME TO resource;

--changeset trytofixme:drop_post_id_from_resource_20250612
ALTER TABLE resource
DROP COLUMN post_id;