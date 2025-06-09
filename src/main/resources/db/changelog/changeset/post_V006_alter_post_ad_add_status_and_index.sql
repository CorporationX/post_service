ALTER TABLE post_ad
    ADD COLUMN status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    ADD CONSTRAINT post_ad_status_check
        CHECK (status IN ('ACTIVE', 'EXPIRED'));

CREATE INDEX idx_post_ad_status_when_appearances_zero ON post_ad (status)
    WHERE appearances_left = 0;

CREATE INDEX idx_post_ad_status_when_appearances_left ON post_ad (status, end_date)
    WHERE appearances_left != 0;