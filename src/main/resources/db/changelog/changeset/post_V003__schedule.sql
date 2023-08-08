CREATE TABLE scheduled_tasks (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    entity_type varchar(255),
    task_type varchar(255),
    entity_id bigint UNIQUE,
    retry_count int DEFAULT 0,
    status varchar(255) DEFAULT 'NEW',
    scheduled_at timestamptz
);

CREATE INDEX scheduled_tasks_scheduled_at_idx
    ON scheduled_tasks (entity_id) WHERE status = 'NEW' OR status = 'ERROR';