alter table device drop foreign key fk_device_user_id;

drop index fk_device_user_id on device;

alter table device modify user_id bigint not null;