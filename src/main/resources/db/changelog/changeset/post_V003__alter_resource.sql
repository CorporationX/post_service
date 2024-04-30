ALTER TABLE resource
ADD post_id bigint,
CONSTRAINT fk_post_id FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE;