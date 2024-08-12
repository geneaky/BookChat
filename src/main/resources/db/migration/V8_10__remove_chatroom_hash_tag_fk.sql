alter table chat_room_hash_tag drop foreign key fk_chat_room_hash_tag_chat_room_id;

alter table chat_room_hash_tag drop foreign key fk_chat_room_hash_tag_hash_tag_id;

drop index fk_chat_room_hash_tag_hash_tag_id on chat_room_hash_tag;

alter table chat_room_hash_tag modify chat_room_id bigint not null;

alter table chat_room_hash_tag modify hash_tag_id bigint not null;