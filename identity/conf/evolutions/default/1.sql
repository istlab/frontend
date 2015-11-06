# --- First database schema

# --- !Ups

set ignorecase true;

create table guardian_user(
  id                         varchar(255) not null,
  email                      varchar(255) not null unique,
  data_subject_id            varchar(255) not null,
  constraint pk_guardian_user primary key (id));

create table data_subject(
  id                         varchar(255) not null,
  eagent                     varchar(255) not null,
  client_id                  varchar(255) not null unique,
  client_secret              varchar(255) not null,
  constraint pk_data_subject primary key (id));

create sequence guardian_user_seq start with 1000;

create sequence data_subject_seq start with 1000;

alter table guardian_user add constraint fk_gu_ds_1 foreign key (data_subject_id) references data_subject (id) on delete restrict on update restrict;
create index ix_gu_dj_1 on guardian_user (data_subject_id);

insert into data_subject(id, eagent, client_id, client_secret) VALUES (
  'test-id2',
  'localhost:9443',
  'dbf21b3a-92e0-44cc-92fc-ba27ca07119e',
  '46f01f9e1ef22a9ccdf0c7ab7f4177ebabe5d58094637c91a8da73e026dfc6b28a4462059dba11fec0c636f9f461508a0637801b482d9e4e68f7ff2a0e257ca4'
);

insert into guardian_user(id, email, data_subject_id) VALUES (
  'test-id2',
  'theosotr@gmail.com',
  'test-id2'
);

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists guardian_user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists guardian_user_seq;

drop sequence if exists data_subject_seq;
