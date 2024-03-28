ALTER TABLE album
ADD COLUMN visibility VARCHAR(20) DEFAULT 'PUBLIC' NOT NULL

CREATE TABLE album_users (
    album_id bigint NOT NULL,
    user_id bigint NOT NULL

    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES album (id)
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);