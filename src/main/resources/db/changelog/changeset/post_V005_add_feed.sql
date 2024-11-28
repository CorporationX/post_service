-- потом убрать удаление
drop table if exists feed;
create table if not exists feed (
    id bigserial primary key,
    user_id bigint not null,
    post_id bigint not null,
    created_at timestamp not null default current_timestamp,

    constraint feed_post_id_fk foreign key(post_id) references post(id),
    constraint feed_user_id_post_id_unique unique (user_id, post_id)
)