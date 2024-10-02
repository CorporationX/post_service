ALTER TABLE album
    ALTER COLUMN visibility TYPE VARCHAR(255)
    USING (
        CASE
            WHEN visibility = 0 THEN 'only_author'
            WHEN visibility = 1 THEN 'allowed_users'
            WHEN visibility = 2 THEN 'subscribers'
            ELSE 'all'
        END
    ),
    ALTER COLUMN visibility SET DEFAULT 'only_author';