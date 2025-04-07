CREATE TABLE tag
(
    id           bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name         varchar(100)              NOT NULL,
    rating       bigint,
    created_at   timestamptz DEFAULT current_timestamp,
    creator_id   bigint                    NOT NULL
);

CREATE TABLE post_tag
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    tag_id     bigint NOT NULL,
    post_id    bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT fk_tag_id FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);