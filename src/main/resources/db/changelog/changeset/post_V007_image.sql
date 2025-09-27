CREATE TABLE post_image (
    key     varchar(4096) NOT NULL,
    post_id bigint,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
)