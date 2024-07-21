CREATE TABLE user_visibility (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       user_id bigint NOT NULL,
                       album_id bigint,
                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp,

                       CONSTRAINT fk_comment_id FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
);

ALTER TABLE album
    ADD COLUMN if not exists visibility_type varchar(128),
    ADD COLUMN if not exists visibility_users bigint;