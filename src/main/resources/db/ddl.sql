drop schema if exists test;
create schema test;
use test;

create table user
(
    id                         bigint auto_increment primary key,
    name                       varchar(255)                                 null,
    provider                   varchar(255)                                 null,
    email                      varchar(255)                                 null,
    nickname                   varchar(20)                                 null,
    default_profile_image_type int                                          null,
    profile_image_url          varchar(255)                                 null,
    role                       int                                          null,
    status                     enum ('ACTIVE', 'INACTIVE') default 'ACTIVE' null,
    created_at                 datetime(6)                                  null,
    updated_at                 datetime(6)                                  null,
    constraint uk_user_nickname unique (nickname)
);

create table if not exists refresh_token
(
    id            bigint auto_increment primary key,
    user_id       bigint      null,
    refresh_token text        null,
    created_at    datetime(6) null,
    updated_at    datetime(6) null,
    constraint uk_refresh_token_user_id unique (user_id)
);

create table if not exists user_reading_tastes
(
    user_id        bigint not null,
    reading_tastes int    null,
    constraint fk_user_reading_tastes_user_id foreign key (user_id) references user (id)
);

create table if not exists book
(
    id                   bigint auto_increment primary key,
    isbn                 varchar(255) not null,
    title                varchar(255) null,
    book_cover_image_url varchar(255) null,
    publisher            varchar(255) null,
    publish_at           date         not null,
    created_at           datetime(6)  null,
    updated_at           datetime(6)  null,
    constraint uq_book_isbn_publish_at unique (isbn, publish_at)
);

create table if not exists book_authors (
                              book_id bigint not null,
                              authors varchar(255),
                              constraint fk_book_authors_book_id foreign key (book_id) references book (id)
);

create table if not exists book_shelf
(
    id             bigint auto_increment primary key,
    book_id        bigint       not null,
    user_id        bigint       not null,
    reading_status varchar(255) null,
    star           varchar(255) null,
    pages          int          null,
    created_at     datetime(6)  null,
    updated_at     datetime(6)  null,
    constraint uq_book_shelf_user_id_book_id unique (user_id, book_id)
);

create table if not exists agony
(
    id             bigint auto_increment primary key,
    book_shelf_id  bigint       not null,
    title          varchar(500) null,
    hex_color_code varchar(255) null,
    created_at     datetime(6)  null,
    updated_at     datetime(6)  null
);

create table if not exists agony_record
(
    id         bigint auto_increment primary key,
    agony_id   bigint       not null,
    title      varchar(500) null,
    content    text null,
    created_at datetime(6)  null,
    updated_at datetime(6)  null
);

create table if not exists chat_room
(
    id                      bigint auto_increment primary key,
    book_id                 bigint       not null,
    room_sid                varchar(255) null,
    room_name               varchar(30) null,
    room_size               int          not null,
    default_room_image_type int          not null,
    room_image_uri          varchar(255) null,
    created_at              datetime(6)  null,
    updated_at              datetime(6)  null,
    constraint uk_chat_room_room_sid unique (room_sid)
);

create table if not exists hash_tag
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)  null,
    updated_at datetime(6)  null,
    tag_name   varchar(50) null,
    constraint uk_hash_tag_tag_name
        unique (tag_name)
);

create table if not exists chat_room_hash_tag
(
    id           bigint auto_increment primary key,
    chat_room_id bigint      not null,
    hash_tag_id  bigint      not null,
    created_at   datetime(6) null,
    updated_at   datetime(6) null,
    constraint uk_chat_room_hash_tag_chat_room_id_hash_tag_id unique (chat_room_id, hash_tag_id)
);

create table if not exists chat_room_blocked_user
(
    id           bigint auto_increment primary key,
    chat_room_id bigint not null,
    user_id      bigint not null,
    constraint uk_chat_room_blocked_user_user_id_chat_room_id unique (user_id, chat_room_id)
);

create table if not exists chat
(
    id           bigint auto_increment primary key,
    chat_room_id bigint      not null,
    user_id      bigint      null,
    message      text        null,
    created_at   datetime(6) null,
    updated_at   datetime(6) null
);

create table if not exists participant
(
    id                 bigint auto_increment primary key,
    chat_room_id       bigint       not null,
    user_id            bigint       not null,
    is_connected       tinyint(1)   null,
    participant_status varchar(255) not null,
    constraint uk_participant_user_id_chat_room_id unique (user_id, chat_room_id)
);

create table if not exists book_report
(
    id            bigint auto_increment primary key,
    book_shelf_id bigint       not null,
    title         varchar(500) null,
    content       text     null,
    created_at    datetime(6)  null,
    updated_at    datetime(6)  null
);

create table if not exists device
(
    id           bigint auto_increment primary key,
    user_id      bigint       not null,
    device_token varchar(255) not null,
    fcm_token    varchar(255) null,
    created_at   datetime(6)  null,
    updated_at   datetime(6)  null
);

create table if not exists scrap
(
    id            bigint auto_increment primary key,
    book_shelf_id bigint      not null,
    scrap_content text        null,
    created_at    datetime(6) null,
    updated_at    datetime(6) null
);