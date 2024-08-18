alter table scrap drop foreign key fk_scrap_book_shelf_id;

drop index fk_scrap_book_shelf_id on scrap;

alter table scrap modify book_shelf_id bigint not null;