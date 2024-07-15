alter table book_shelf
drop foreign key fk_book_shelf_book_id;

alter table book_shelf
drop foreign key fk_book_shelf_user_id;

drop index fk_book_shelf_book_id on book_shelf;

alter table book_shelf
    modify book_id bigint not null;

alter table book_shelf
    modify user_id bigint not null;