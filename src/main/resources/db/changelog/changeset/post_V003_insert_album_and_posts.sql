INSERT INTO album (title, description, author_id, created_at, updated_at)
VALUES
    ('test1', 'test', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('test2', 'test', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('test1', 'test', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('test1', 'test', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('test2', 'test', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO post(content, author_id, project_id, published, published_at, scheduled_at, deleted, created_at, updated_at)
VALUES
    ('test', 1, 1, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('test2', 2, 2, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
