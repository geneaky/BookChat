alter table book_report add book_shelf_id bigint not null;

alter table book_shelf drop foreign key fk_book_shelf_book_report_id;

drop index fk_book_shelf_book_report_id on book_shelf;

alter table book_shelf drop column book_report_id;