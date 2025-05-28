ALTER TABLE post
ADD CONSTRAINT author_or_project_check
CHECK (
    (author_id IS NULL AND project_id IS NOT NULL)
    OR (author_id IS NOT NULL AND project_id IS NULL)
);