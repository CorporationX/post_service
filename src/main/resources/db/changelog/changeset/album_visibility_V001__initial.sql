ALTER TABLE album
ADD COLUMN visibility VARCHAR(20) DEFAULT 'PUBLIC' NOT NULL;

CREATE TABLE album_users (
    album_id bigint NOT NULL,
    user_id  bigint NOT NULL,

    CONSTRAINT fk_album_users_album FOREIGN KEY (album_id) REFERENCES album (id)
);