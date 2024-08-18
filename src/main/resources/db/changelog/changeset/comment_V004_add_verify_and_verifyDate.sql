ALTER TABLE comment
    add column verify boolean not null default false,
add column verify_date timestamptz;