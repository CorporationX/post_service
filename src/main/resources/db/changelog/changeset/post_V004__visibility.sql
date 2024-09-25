CREATE TABLE IF NOT EXISTS album_visibility (
    id bigint GENERATED ALWAYS AS IDENTITY UNIQUE,
    album_id bigint NOT NULL,
    user_id bigint NOT NULL,

    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
);

ALTER TABLE album
ADD COLUMN visibility VARCHAR(50) NOT NULL DEFAULT 'all_users';

