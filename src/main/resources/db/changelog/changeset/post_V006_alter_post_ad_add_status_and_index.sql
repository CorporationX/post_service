ALTER TABLE post_ad
    ADD COLUMN status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE';

CREATE INDEX idx_ad_appearances_zero ON post_ad (appearances_left)
    WHERE appearances_left = 0;

CREATE INDEX idx_ad_end_date ON post_ad (end_date)
