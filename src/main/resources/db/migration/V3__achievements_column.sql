create table achievements_unlocked
(
    id             int auto_increment primary key,
    achievement_id varchar(255)                          not null,
    user           varchar(255)                          not null,
    commit_id      varchar(255)                          not null,
    created_at     timestamp default current_timestamp() not null
);

create index achievement_id
    on achievements_unlocked (achievement_id);

create index user
    on achievements_unlocked (user);