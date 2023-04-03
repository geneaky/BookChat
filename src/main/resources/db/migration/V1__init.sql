create table user (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    default_profile_image_type integer,
    email varchar(255),
    name varchar(255),
    nickname varchar(255),
    profile_image_url varchar(255),
    provider varchar(255),
    role integer,
    primary key (id),
    constraint uk_user_nickname unique (nickname)
) engine=InnoDB;

create table refresh_token (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    refresh_token TEXT,
    user_id bigint,
    primary key (id),
    constraint uk_refresh_token_user_id unique (user_id)
) engine=InnoDB;

create table user_reading_tastes (
    user_id bigint not null,
    reading_tastes integer,
    constraint fk_user_reading_tastes_user_id foreign key (user_id) references user (id)
) engine=InnoDB;

create table book (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    book_cover_image_url varchar(255),
    isbn varchar(255) not null,
    publish_at date not null,
    publisher varchar(255),
    title varchar(255),
    primary key (id),
    constraint uq_book_isbn_publish_at unique (isbn, publish_at)
) engine=InnoDB;

create table book_authors (
    book_id bigint not null,
    authors varchar(255),
    constraint fk_book_authors_book_id foreign key (book_id) references book (id)
) engine=InnoDB;

create table book_report (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    content varchar(255),
    title varchar(255),
    primary key (id)
) engine=InnoDB;

create table book_shelf (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    pages integer,
    reading_status varchar(255),
    star varchar(255),
    book_id bigint,
    book_report_id bigint,
    user_id bigint,
    primary key (id),
    constraint uq_book_shelf_user_id_book_id unique(user_id, book_id),
    constraint fk_book_shelf_book_id foreign key (book_id) references book (id),
    constraint fk_book_shelf_book_report_id foreign key (book_report_id) references book_report (id),
    constraint fk_book_shelf_user_id foreign key (user_id) references user (id)
) engine=InnoDB;

create table agony (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    hex_color_code varchar(255),
    title varchar(255),
    book_shelf_id bigint,
    primary key (id),
    constraint fk_agony_book_shelf_id foreign key (book_shelf_id) references book_shelf(id)
) engine=InnoDB;

create table agony_record (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    content varchar(255),
    title varchar(255),
    agony_id bigint,
    primary key (id),
    constraint fk_agony_record_agony_id foreign key (agony_id) references agony(id)
) engine=InnoDB;

create table chat_room (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    default_room_image_type integer not null,
    room_image_uri varchar(255),
    room_name varchar(255),
    room_sid varchar(255),
    room_size integer not null,
    book_id bigint,
    host_id bigint,
    primary key (id),
    constraint uk_chat_room_room_sid unique (room_sid),
    constraint fk_chat_room_book_id foreign key (book_id) references book (id),
    constraint fk_chat_room_host_id foreign key (host_id) references user (id)
) engine=InnoDB;

create table hash_tag (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    tag_name varchar(255),
    primary key (id),
    constraint uk_hash_tag_tag_name unique (tag_name)
) engine=InnoDB;

create table chat_room_hash_tag (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    chat_room_id bigint,
    hash_tag_id bigint,
    primary key (id),
    constraint uk_chat_room_hash_tag_chat_room_id_hash_tag_id unique (chat_room_id, hash_tag_id),
    constraint fk_chat_room_hash_tag_chat_room_id foreign key (chat_room_id) references chat_room (id),
    constraint fk_chat_room_hash_tag_hash_tag_id foreign key (hash_tag_id) references hash_tag (id)
) engine=InnoDB;

create table chat_room_blocked_user (
    id bigint not null auto_increment,
    chat_room_id bigint,
    user_id bigint,
    primary key (id),
    constraint uk_chat_room_blocked_user_user_id_chat_room_id unique (user_id, chat_room_id),
    constraint fk_chat_room_blocked_user_chat_room_id foreign key (chat_room_id) references chat_room (id),
    constraint fk_chat_room_blocked_user_user_id foreign key (user_id) references user (id)
) engine=InnoDB;

create table chat (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    message varchar(255),
    chat_room_id bigint,
    user_id bigint,
    primary key (id),
    constraint fk_chat_chat_room_id foreign key (chat_room_id) references chat_room (id),
    constraint fk_chat_user_id foreign key (user_id) references user (id)
) engine=InnoDB;

create table participant (
    id bigint not null auto_increment,
    participant_status varchar(255) not null,
    chat_room_id bigint,
    user_id bigint,
    primary key (id),
    constraint uk_participant_user_id_chat_room_id unique (user_id, chat_room_id),
    constraint fk_participant_chat_room_id foreign key (chat_room_id) references chat_room (id),
    constraint fk_participant_user_id foreign key (user_id) references user (id)
) engine=InnoDB;