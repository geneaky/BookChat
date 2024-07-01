alter table agony
    modify book_shelf_id bigint not null;

alter table agony
drop foreign key fk_agony_book_shelf_id;

drop index fk_agony_book_shelf_id on agony;

alter table agony_record
    modify agony_id bigint not null;

alter table agony_record
drop foreign key fk_agony_record_agony_id;

drop index fk_agony_record_agony_id on agony_record;