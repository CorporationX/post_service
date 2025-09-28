DROP TABLE IF EXISTS favorite_albums CASCADE;
DROP TABLE IF EXISTS post_album CASCADE;
DROP TABLE IF EXISTS album CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS comment CASCADE;
DROP TABLE IF EXISTS post CASCADE;
DROP TABLE IF EXISTS user_subscription CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE user_subscription (
                                   id BIGSERIAL PRIMARY KEY,
                                   follower_id BIGINT NOT NULL,
                                   followee_id BIGINT NOT NULL,
                                   status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                   created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                   updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE post (
                      id BIGSERIAL PRIMARY KEY,
                      content TEXT,
                      author_id BIGINT NOT NULL,
                      project_id BIGINT,
                      published BOOLEAN DEFAULT FALSE,
                      published_at TIMESTAMP,
                      scheduled_at TIMESTAMP,
                      deleted BOOLEAN DEFAULT FALSE,
                      created_at TIMESTAMP DEFAULT NOW(),
                      updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE comment (
                         id BIGSERIAL PRIMARY KEY,
                         post_id BIGINT NOT NULL,
                         author_id BIGINT NOT NULL,
                         content TEXT NOT NULL,
                         created_at TIMESTAMP DEFAULT NOW(),
                         updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE likes (
                       id BIGSERIAL PRIMARY KEY,
                       post_id BIGINT,
                       comment_id BIGINT,
                       user_id BIGINT NOT NULL,
                       created_at TIMESTAMP DEFAULT NOW(),

                       CONSTRAINT fk_likes_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                       CONSTRAINT fk_likes_comment FOREIGN KEY (comment_id) REFERENCES comment(id) ON DELETE CASCADE,
                       CONSTRAINT chk_post_or_comment CHECK (
                           (post_id IS NOT NULL AND comment_id IS NULL) OR
                           (post_id IS NULL AND comment_id IS NOT NULL)
                           )
);

CREATE TABLE album (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       user_id BIGINT NOT NULL,
                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE post_album (
                            id BIGSERIAL PRIMARY KEY,
                            post_id BIGINT NOT NULL,
                            album_id BIGINT NOT NULL,
                            created_at TIMESTAMP DEFAULT NOW(),
                            updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE favorite_albums (
                                 id BIGSERIAL PRIMARY KEY,
                                 user_id BIGINT NOT NULL,
                                 album_id BIGINT NOT NULL,
                                 created_at TIMESTAMP DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_post_updated_at
    BEFORE UPDATE ON post
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_comment_updated_at
    BEFORE UPDATE ON comment
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_album_updated_at
    BEFORE UPDATE ON album
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_post_album_updated_at
    BEFORE UPDATE ON post_album
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

INSERT INTO users (username, email) VALUES
                                        ('alice', 'alice@example.com'),
                                        ('bob', 'bob@example.com'),
                                        ('charlie', 'charlie@example.com'),
                                        ('david', 'david@example.com');

INSERT INTO user_subscription (follower_id, followee_id, status) VALUES
                                                                     (1, 2, 'ACCEPTED'),
                                                                     (2, 1, 'ACCEPTED'),
                                                                     (3, 1, 'ACCEPTED'),
                                                                     (4, 1, 'PENDING');

INSERT INTO post (content, author_id, published) VALUES
                                                     ('First post from Alice', 1, TRUE),
                                                     ('Another post from Alice', 1, TRUE),
                                                     ('Post from Bob', 2, TRUE),
                                                     ('Charlie says hello', 3, TRUE);

INSERT INTO comment (post_id, author_id, content) VALUES
                                                      (1, 2, 'Nice post Alice!'),
                                                      (1, 3, 'Agreed, well written.'),
                                                      (3, 1, 'Good post Bob!');

INSERT INTO likes (post_id, user_id) VALUES
                                         (1, 2),
                                         (1, 3),
                                         (2, 2),
                                         (3, 1);

INSERT INTO likes (comment_id, user_id) VALUES
                                            (1, 1),
                                            (2, 2);

INSERT INTO album (name, user_id) VALUES
                                      ('Alice Album', 1),
                                      ('Bob Album', 2);

INSERT INTO post_album (post_id, album_id) VALUES
                                               (1, 1),
                                               (3, 2);

INSERT INTO favorite_albums (user_id, album_id) VALUES
                                                    (2, 1),
                                                    (3, 2);
