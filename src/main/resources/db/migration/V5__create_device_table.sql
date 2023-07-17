create table device (
    id bigint not null auto_increment,
    device_token varchar(255) not null,
    fcm_token varchar(255),
    user_id bigint,
    primary key (id),
    constraint fk_device_user_id foreign key (user_id) references user (id)
) engine=InnoDB;