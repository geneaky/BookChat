alter table chat drop foreign key fk_chat_chat_room_id;

alter table chat drop foreign key fk_chat_user_id;

drop index fk_chat_chat_room_id on chat;

drop index fk_chat_user_id on chat;

alter table chat modify chat_room_id bigint not null;

alter table chat modify user_id bigint not null;