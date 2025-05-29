CREATE TABLE analytic
(
    id           bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    author_id    bigint,
    receiver_id  bigint,
    event_type   smallint    DEFAULT 0     NOT NULL,
    created_at   timestamptz
);