CREATE TABLE users_with_access (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    album_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE
);