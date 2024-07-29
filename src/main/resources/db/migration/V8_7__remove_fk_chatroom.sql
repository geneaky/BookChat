alter table chat_room drop foreign key fk_chat_room_host_id;

drop index fk_chat_room_host_id on chat_room;

alter table chat_room modify host_id bigint not null;