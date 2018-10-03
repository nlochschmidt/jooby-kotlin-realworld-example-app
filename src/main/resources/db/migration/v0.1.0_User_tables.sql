create extension citext;

create table users (
    id bytea primary key,
    email citext unique not null,
    username text not null,
    bio text not null,
    image text
);

create table user_credentials (
    user_id bytea references users(id) on delete cascade,
    email citext primary key,
    password_type text not null,
    password_hash bytea not null,
    password_salt bytea not null,
    password_iterations int not null
)
