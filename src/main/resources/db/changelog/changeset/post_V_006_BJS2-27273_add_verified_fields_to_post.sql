-- ChangeSet: add-verified-fields-to-post
ALTER TABLE post
    ADD COLUMN verified BOOLEAN,
    ADD COLUMN verified_date TIMESTAMP;

COMMENT ON COLUMN post.verified IS 'Indicates if the post has been moderated';
COMMENT ON COLUMN post.verified_date IS 'Date and time when the post was moderated';