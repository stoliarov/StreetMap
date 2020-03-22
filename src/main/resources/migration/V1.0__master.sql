create table if not exists node (
    id         bigint primary key,
    version    varchar(255),
    date_time  timestamp with time zone,
    uid        bigint,
    user_name  varchar(255),
    change_set bigint,
    lat        double precision,
    lon        double precision
);

create table if not exists way (
    id         bigint primary key,
    version    varchar(255),
    date_time  timestamp with time zone,
    uid        bigint,
    user_name  varchar(255),
    change_set bigint
);


create table if not exists relation (
    id         bigint primary key,
    version    varchar(255),
    date_time  timestamp with time zone,
    uid        bigint,
    user_name  varchar(255),
    change_set bigint
);

create table if not exists node_of_way (
    id      bigint primary key,
    way_id  bigint,
    node_id bigint,
    order_  bigint,

    foreign key (way_id) references way (id)
        on delete cascade
        on update cascade,
    foreign key (node_id) references node (id)
        on delete cascade
        on update cascade
);

create table if not exists way_tag (
    id     bigint primary key,
    key_   varchar(255),
    value  varchar(255),
    way_id bigint,

    foreign key (way_id) references way (id)
        on delete cascade
        on update cascade
);

create table if not exists relation_tag (
    id          bigint primary key,
    key_        varchar(255),
    value       varchar(255),
    relation_id bigint,

    foreign key (relation_id) references relation (id)
        on delete cascade
        on update cascade
);

create table if not exists relation_member (
    id          bigint primary key,
    type        varchar(255),
    role        varchar(255),
    relation_id bigint,

    foreign key (relation_id) references relation (id)
        on delete cascade
        on update cascade
);
