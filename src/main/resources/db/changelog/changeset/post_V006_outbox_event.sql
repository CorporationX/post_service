create table if not exists outbox_event
(
    id             bigserial primary key,
    event_id       uuid         not null,
    event_type     varchar(100) not null,
    aggregate_type varchar(50)  not null,
    aggregate_id   bigint       not null,
    payload        jsonb        not null,
    status         varchar(20)  not null default 'NEW',
    attempts       int          not null default 0,
    last_error     text,
    created_at     timestamptz  not null default now(),
    updated_at     timestamptz  not null default now(),
    sent_at        timestamptz
);

create index if not exists idx_outbox_new on outbox_event (status, created_at);
