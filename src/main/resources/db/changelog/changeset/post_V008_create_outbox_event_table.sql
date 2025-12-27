CREATE TABLE outbox_event (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(64) NOT NULL,
    aggregate_id BIGINT,
    payload JSONB NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'NEW',
    source_service VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_outbox_status ON outbox_event(status);
CREATE INDEX idx_outbox_source_service ON outbox_event(source_service);