ALTER TABLE album
    ADD COLUMN visibility varchar(255) NOT NULL;

create table if not exists visible_albums (
    album_id bigint NOT NULL,
    user_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,

    PRIMARY KEY (album_id, user_id),
    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
);