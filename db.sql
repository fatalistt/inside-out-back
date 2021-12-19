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