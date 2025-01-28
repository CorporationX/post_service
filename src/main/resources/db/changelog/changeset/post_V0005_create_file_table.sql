create table file (
	id bigint generated always as identity primary key,
	key VARCHAR(64) not null,
	size bigint not null,
	created_at timestamptz DEFAULT current_timestamp,
	type VARCHAR(50)
);

CREATE TABLE comment_file (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    comment_id BIGINT NOT NULL,
    file_id BIGINT NOT NULL,

    CONSTRAINT fk_comment_id FOREIGN KEY (comment_id) REFERENCES comment (id) ON DELETE CASCADE,
    CONSTRAINT fk_file_id FOREIGN KEY (file_id) REFERENCES file (id) ON DELETE CASCADE
);
