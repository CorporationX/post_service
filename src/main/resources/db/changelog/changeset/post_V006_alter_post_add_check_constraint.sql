ALTER TABLE post
ADD CONSTRAINT chk_author_or_project
CHECK (
    (author_id IS NULL AND project_id IS NOT NULL)
    OR (author_id IS NOT NULL AND project_id IS NULL)
);