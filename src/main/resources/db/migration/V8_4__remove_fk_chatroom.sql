alter table chat_room drop foreign key fk_chat_room_book_id;

drop index fk_chat_room_book_id on chat_room;

alter table chat_room modify book_id bigint not null;