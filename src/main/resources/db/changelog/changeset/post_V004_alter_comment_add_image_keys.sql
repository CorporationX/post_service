ALTER TABLE comment
    ADD COLUMN large_image_file_key VARCHAR(255),
    ADD COLUMN small_image_file_key VARCHAR(255);