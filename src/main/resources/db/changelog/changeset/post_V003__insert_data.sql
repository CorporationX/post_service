INSERT INTO post(content, author_id, project_id, published, published_at, created_at, deleted)
VALUES
    ('Post about Spring and Hibernate', 1, 1, true, '2024-04-20 10:23:54', '2024-04-20 9:23:54', false),
    ('Post about Jenkins', 2, 1, true, '2024-04-21 13:12:54', '2024-04-21 12:23:54', false),
    ('Some post about Kafka', 3, 2, true, '2024-04-23 19:15:54', '2024-04-23 18:15:54', false);

INSERT INTO comment (content, author_id, post_id, created_at)
VALUES
    ('This post is great!', 5, 1, '2024-04-20 20:20:12'),
    ('It is very helpful for me. Thank you!', 6, 1, '2024-04-22 12:10:12'),
    ('Jenkins is not simple as I thought', 7, 2, '2024-04-22 14:20:12'),
    ('Kafka is the best message broker', 5, 3, '2024-04-24 15:33:32'),
    ('Thanks for this post', 8, 3, '2024-04-25 11:18:45');

INSERT INTO likes(post_id, comment_id, user_id, created_at)
VALUES
    (1, 2, 3, '2024-04-22 13:10:29'),
    (2, 3, 9, '2024-04-22 14:30:16'),
    (2, 3, 1, '2024-04-22 14:40:12'),
    (3, 4, 2, '2024-04-24 16:33:32'),
    (3, 4, 1, '2024-04-24 19:48:11');

INSERT INTO album(title, description, author_id, created_at)
VALUES
    ('Programming on Java', 'All about Java and IT', 1, '2022-01-01 10:00:01');

INSERT INTO post_album(post_id, album_id, created_at)
VALUES
    (1, 1, '2024-04-20 10:25:54'),
    (2, 1, '2024-04-21 13:17:54'),
    (3, 1, '2024-04-23 19:20:54');