create table "user"
(
    id   serial primary key,
    name varchar(2000)
);

create table user_login_password
(
    id            integer primary key references "user",
    login         varchar(100) unique,
    password_hash varchar(2000)
);

create table role
(
    id   serial primary key,
    name varchar(2000) unique
);

create table user_role
(
    id      serial primary key,
    user_id integer references "user",
    role_id integer references role
);

insert into "user"
values (1, 'Радость');
insert into user_login_password
values (1, 'happiness',
        '1000:80452b5cf26f5721048183a6b3d9c1f5:a6cd5a5c7ebe0f6fb2159592f6f8ebaedc4ddfb5a7f3103e1ee3b55f1624d48f9037e76c38c02422b9dbada7aa94e3f5dadc04f9be34212888b7bfa84897855e');
insert into role
values (1, 'emotion');
insert into user_role(user_id, role_id)
values (1, 1);

create table file
(
    id           serial primary key,
    content      bytea,
    content_type varchar(2000)
);

create table short_memory
(
    id          serial primary key,
    date        timestamptz,
    type        varchar(2000),
    description varchar(2000),
    rating      integer
);

create index idx_short_memory
    on short_memory (date, type, rating);

create table short_memory_file
(
    id        bigserial primary key,
    memory_id integer references short_memory,
    file_id   integer references file
);

create table long_memory
(
    id          integer primary key,
    date        timestamptz,
    type        varchar(2000),
    description varchar(2000),
    rating      integer
);

create index idx_long_memory
    on long_memory (date, type, rating);

create table long_memory_file
(
    id        bigserial primary key,
    memory_id integer references long_memory,
    file_id   integer references file
);
