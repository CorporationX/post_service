ALTER TABLE resource
    ADD COLUMN post_id INTEGER REFERENCES post(id);
