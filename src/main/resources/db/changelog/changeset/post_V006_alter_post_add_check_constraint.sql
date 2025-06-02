ALTER TABLE post
ADD CONSTRAINT check_author_xor_project
CHECK (
    (author_id IS NULL AND project_id IS NOT NULL)
    OR (author_id IS NOT NULL AND project_id IS NULL)
);