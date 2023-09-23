ALTER TABLE album ADD COLUMN visibility VARCHAR;

CREATE TABLE allowed_user
(
    user_id  BIGINT,
    album_id BIGINT,
    PRIMARY KEY (user_id, album_id),
    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES album (id)
);