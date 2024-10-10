ALTER TABLE album
    ADD COLUMN visibility VARCHAR(32) NOT NULL DEFAULT 'ALL_USERS';

CREATE TABLE album_chosen_users (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    album_id bigint NOT NULL,
    user_id bigint NOT NULL,

    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);