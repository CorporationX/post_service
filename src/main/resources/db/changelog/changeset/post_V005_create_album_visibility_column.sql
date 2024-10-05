ALTER TABLE album
ADD COLUMN visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC';

CREATE TABLE album_visibility_users (
    album_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_album FOREIGN KEY (album_id) REFERENCES album(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(id)
);