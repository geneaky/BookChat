alter table chat_room_blocked_user drop foreign key fk_chat_room_blocked_user_chat_room_id;

alter table chat_room_blocked_user drop foreign key fk_chat_room_blocked_user_user_id;

drop index fk_chat_room_blocked_user_chat_room_id on chat_room_blocked_user;

alter table chat_room_blocked_user modify chat_room_id bigint not null;

alter table chat_room_blocked_user modify user_id bigint not null;