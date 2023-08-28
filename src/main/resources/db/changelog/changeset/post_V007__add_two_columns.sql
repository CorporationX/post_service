alter table album
    add column visibility smallint default 0 not null,
    add column allowed_users json;