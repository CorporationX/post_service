ALTER TABLE album
ADD COLUMN visibility VARCHAR(20) DEFAULT 'PUBLIC' NOT NULL

CREATE TABLE album_visibility (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    album_id bigint NOT NULL,
    users_ids_list bigint[]
);