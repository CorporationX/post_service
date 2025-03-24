INSERT INTO post (
    content,
    author_id,
    project_id,
    published,
    published_at,
    scheduled_at,
    deleted,
    created_at,
    updated_at
)
VALUES
    ('Текст поста 1', 1, 1, true, '2025-02-14 12:00:00+00', '2025-02-17 09:00:00+00', false, current_timestamp, current_timestamp),
    ('Текст поста 2', 2, 2, true, '2025-02-16 12:00:00+00', '2025-02-18 15:00:00+00', false, current_timestamp, current_timestamp);
