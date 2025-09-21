CREATE TABLE post (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    content varchar(4096) NOT NULL,
    author_id bigint,
    project_id bigint,
    published boolean DEFAULT false NOT NULL,
    published_at timestamptz,
    scheduled_at timestamptz,
    deleted boolean DEFAULT false NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE comment (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    content varchar(4096) NOT NULL,
    author_id bigint NOT NULL,
    post_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);

CREATE TABLE likes (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    post_id bigint,
    comment_id bigint,
    user_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_id FOREIGN KEY (comment_id) REFERENCES comment (id) ON DELETE CASCADE
);

CREATE TABLE album (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    title varchar(64) NOT NULL,
    description varchar(4096),
    author_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

CREATE UNIQUE INDEX album_author_title_idx ON album (author_id, title);

CREATE TABLE post_album (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    post_id bigint NOT NULL,
    album_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
);

CREATE TABLE favorite_albums (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    album_id bigint NOT NULL,
    user_id bigint NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_album_id FOREIGN KEY (album_id) REFERENCES album (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
                                     id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                     username varchar(100) NOT NULL UNIQUE,
    email varchar(255) NOT NULL UNIQUE,
    display_name varchar(200),
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
    );

CREATE TABLE IF NOT EXISTS user_subscription (
                                                 id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                                 follower_id bigint NOT NULL,
                                                 followee_id bigint NOT NULL,
                                                 status varchar(20) NOT NULL DEFAULT 'PENDING',
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT unique_follower_followee UNIQUE (follower_id, followee_id)
    );

CREATE INDEX IF NOT EXISTS idx_post_author_id ON post (author_id);
CREATE INDEX IF NOT EXISTS idx_post_created_at ON post (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_post_published ON post (published);
CREATE INDEX IF NOT EXISTS idx_post_deleted ON post (deleted);
CREATE INDEX IF NOT EXISTS idx_post_author_published_created ON post (author_id, published, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_post_project_id ON post (project_id);
CREATE INDEX IF NOT EXISTS idx_post_scheduled_at ON post (scheduled_at);

CREATE INDEX IF NOT EXISTS idx_user_subscription_followee_id ON user_subscription (followee_id);
CREATE INDEX IF NOT EXISTS idx_user_subscription_follower_id ON user_subscription (follower_id);
CREATE INDEX IF NOT EXISTS idx_user_subscription_status ON user_subscription (status);
CREATE INDEX IF NOT EXISTS idx_user_subscription_followee_status ON user_subscription (followee_id, status);

CREATE INDEX IF NOT EXISTS idx_comment_post_id ON comment (post_id);
CREATE INDEX IF NOT EXISTS idx_comment_author_id ON comment (author_id);
CREATE INDEX IF NOT EXISTS idx_comment_created_at ON comment (created_at DESC);

CREATE INDEX IF NOT EXISTS idx_likes_post_id ON likes (post_id);
CREATE INDEX IF NOT EXISTS idx_likes_comment_id ON likes (comment_id);
CREATE INDEX IF NOT EXISTS idx_likes_user_id ON likes (user_id);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_post_updated_at ON post;
CREATE TRIGGER update_post_updated_at
    BEFORE UPDATE ON post
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_comment_updated_at ON comment;
CREATE TRIGGER update_comment_updated_at
    BEFORE UPDATE ON comment
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_likes_updated_at ON likes;
CREATE TRIGGER update_likes_updated_at
    BEFORE UPDATE ON likes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_album_updated_at ON album;
CREATE TRIGGER update_album_updated_at
    BEFORE UPDATE ON album
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_user_subscription_updated_at ON user_subscription;
CREATE TRIGGER update_user_subscription_updated_at
    BEFORE UPDATE ON user_subscription
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

INSERT INTO users (username, email, display_name) VALUES
                                                      ('john_doe', 'john@example.com', 'John Doe'),
                                                      ('jane_smith', 'jane@example.com', 'Jane Smith'),
                                                      ('bob_wilson', 'bob@example.com', 'Bob Wilson'),
                                                      ('alice_brown', 'alice@example.com', 'Alice Brown')
    ON CONFLICT (username) DO NOTHING;

INSERT INTO user_subscription (follower_id, followee_id, status) VALUES
                                                                     (2, 1, 'ACCEPTED'),
                                                                     (3, 1, 'ACCEPTED'),
                                                                     (4, 1, 'ACCEPTED'),
                                                                     (1, 2, 'ACCEPTED'),
                                                                     (3, 2, 'ACCEPTED'),
                                                                     (4, 2, 'ACCEPTED')
    ON CONFLICT (follower_id, followee_id) DO NOTHING;

INSERT INTO post (content, author_id, project_id, published, published_at) VALUES
                                                                               ('Это содержимое первого поста от John. Всем привет!', 1, NULL, true, CURRENT_TIMESTAMP),
                                                                               ('Работаю над новым проектом. Очень интересные задачи!', 1, 1, true, CURRENT_TIMESTAMP),
                                                                               ('Интересный пост от Jane Smith о разработке', 2, NULL, true, CURRENT_TIMESTAMP),
                                                                               ('Это черновик поста, который еще не готов к публикации', 1, NULL, false, NULL),
                                                                               ('Пост от Bob Wilson про технологии', 3, NULL, true, CURRENT_TIMESTAMP);

INSERT INTO comment (content, author_id, post_id) VALUES
                                                      ('Отличный пост, John!', 2, 1),
                                                      ('Согласен с автором', 3, 1),
                                                      ('Интересная точка зрения', 4, 2),
                                                      ('Jane, как всегда, в точку!', 1, 3);

INSERT INTO likes (post_id, user_id) VALUES
                                         (1, 2),
                                         (1, 3),
                                         (1, 4),
                                         (2, 2),
                                         (3, 1),
                                         (3, 3),
                                         (5, 1),
                                         (5, 2);

INSERT INTO album (title, description, author_id) VALUES
                                                      ('Мои лучшие посты', 'Коллекция моих самых популярных постов', 1),
                                                      ('Технические статьи', 'Посты о программировании и технологиях', 2),
                                                      ('Проектные заметки', 'Документация по текущим проектам', 1);

INSERT INTO post_album (post_id, album_id) VALUES
                                               (1, 1),
                                               (2, 1),
                                               (2, 3),
                                               (3, 2);

COMMIT;