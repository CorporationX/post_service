CREATE TABLE resource_id
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name VARCHAR(128) NOT NULL,
    key VARCHAR(255) NOT NULL,
    size BIGINT,
    type varchar(64) NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,,
    FOREIGN KEY (post_id) REFERENCES post(id)
);