CREATE TABLE processed_events_uuids
(
    uuid       UUID PRIMARY KEY,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX idx_processed_events_uuids ON processed_events_uuids(created_at);