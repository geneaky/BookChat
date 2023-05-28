create table scrap (
    id bigint not null auto_increment,
    scrap_content TEXT,
    created_at    datetime(6),
    updated_at    datetime(6),
    book_shelf_id bigint,
    primary key (id),
    constraint fk_scrap_book_shelf_id foreign key (book_shelf_id) references book_shelf (id)
) engine=InnoDB;
