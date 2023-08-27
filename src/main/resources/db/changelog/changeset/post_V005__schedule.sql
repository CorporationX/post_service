CREATE TABLE scheduled_tasks (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    entity_type varchar(255) NOT NULL,
    task_type varchar(255) NOT NULL,
    entity_id bigint NOT NULL,
    retry_count int NOT NULL,
    status varchar(255) NOT NULL,
    scheduled_at timestamptz
);

CREATE INDEX scheduled_tasks_scheduled_at_idx
    ON scheduled_tasks (entity_id) WHERE status = 'NEW' OR status = 'ERROR';