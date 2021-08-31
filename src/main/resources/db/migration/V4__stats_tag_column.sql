alter table stats
    add tag varchar(255) default 'v1' not null;

create index tag
    on stats (tag)