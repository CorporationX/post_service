INSERT INTO post (content, author_id, published, published_at)
VALUES
    ('post 1', 10, true, '2025-09-01'),
    ('post 2', 10, true, '2025-09-05'),
    ('post 3', 10, false, '2025-09-02'),
    ('post 4', 8, true, '2025-09-03'),
    ('post 5', 8, true, '2025-09-04');

INSERT INTO comment (content, author_id, post_id, created_at)
VALUES
    ('comment 1', 1, 1, '2025-09-01'),
    ('comment 2', 2, 1, '2025-09-02'),

    ('comment 3', 3, 2, '2025-09-03'),
    ('comment 4', 4, 2, '2025-09-02'),
    ('comment 5', 5, 2, '2025-09-01'),
    ('comment 6', 6, 2, '2025-09-04'),

    ('comment 7', 7, 3, '2025-09-01'),
    ('comment 8', 8, 4, '2025-09-01'),

    ('comment 9', 9, 5, '2025-09-03'),
    ('comment 10', 10, 5, '2025-09-04'),
    ('comment 11', 10, 5, '2025-09-01'),
    ('comment 12', 10, 5, '2025-09-02');

INSERT INTO likes (post_id, user_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 4),
    (2, 5);