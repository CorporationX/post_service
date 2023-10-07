CREATE TABLE IF NOT EXISTS picture (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    picture_name varchar(64),
    post_id bigint,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);