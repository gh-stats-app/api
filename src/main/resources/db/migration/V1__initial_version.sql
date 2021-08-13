create table stats
(
    id         int auto_increment primary key,
    repository varchar(255)                          not null,
    action     varchar(255)                          not null,
    created_at timestamp default current_timestamp() not null
);

create index action
    on stats (action);

create index repository
    on stats (repository);