CREATE TABLE hash_tag (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name varchar(4096) NOT NULL,
    author_id bigint,
    project_id bigint,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE post_hash_tag (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    post_id bigint NOT NULL,
    hash_tag_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT fk_hash_tag_id FOREIGN KEY (hash_tag_id) REFERENCES hash_tag (id) ON DELETE CASCADE
);