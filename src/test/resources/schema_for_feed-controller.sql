CREATE TABLE post (
                      id bigint PRIMARY KEY,
                      content varchar(4096) NOT NULL,
                      author_id bigint,
                      project_id bigint,
                      published boolean DEFAULT false NOT NULL,
                      published_at timestamptz,
                      scheduled_at timestamptz,
                      deleted boolean DEFAULT false NOT NULL,
                      created_at timestamptz DEFAULT current_timestamp,
                      updated_at timestamptz DEFAULT current_timestamp,
                      spell_check_completed BOOLEAN DEFAULT TRUE,
                      verified BOOLEAN DEFAULT TRUE,
                      verified_date timestamptz DEFAULT current_timestamp,
                      number_of_views INT DEFAULT 0 NOT NULL,
                      number_of_likes INT DEFAULT 0 NOT NULL,
                      version BIGINT DEFAULT 0 NOT NULL
);

INSERT INTO post (id,content,author_id,published_at,deleted)
VALUES ('45','content of post 45','2','2024-11-27T10:50:00.889178600','false'),
       ('46','content of post 46','2','2024-11-27T10:50:01.889178600','false'),
       ('47','content of post 47','3','2024-11-27T10:50:03.889178600','false');

CREATE TABLE post_ad (
                         id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                         post_id bigint NOT NULL,
                         buyer_id bigint NOT NULL,
                         appearances_left int NOT NULL,
                         start_date timestamptz NOT NULL DEFAULT current_timestamp,
                         end_date timestamptz NOT NULL,

                         CONSTRAINT fk_post_ad_id FOREIGN KEY (post_id) REFERENCES post (id)
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