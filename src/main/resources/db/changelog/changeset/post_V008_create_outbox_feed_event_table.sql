CREATE TABLE outbox_feed_event (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id BIGINT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    version BIGINT
);

CREATE INDEX idx_outbox_feed_event_processed ON outbox_feed_event(processed);