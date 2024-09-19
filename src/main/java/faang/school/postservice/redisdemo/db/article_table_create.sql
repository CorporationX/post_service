CREATE TABLE article
(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    title varchar(64),
    text varchar(128),
    rating double precision,
    hash_tags jsonb
);