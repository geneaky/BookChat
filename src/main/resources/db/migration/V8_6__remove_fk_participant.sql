alter table participant drop foreign key fk_participant_chat_room_id;

alter table participant drop foreign key fk_participant_user_id;

drop index fk_participant_chat_room_id on participant;

alter table participant modify chat_room_id bigint not null;

alter table participant modify user_id bigint not null;