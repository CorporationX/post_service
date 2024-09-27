UPDATE album
SET visibility = UPPER(visibility);

ALTER TABLE album
    ALTER COLUMN visibility SET DEFAULT UPPER('only_author');