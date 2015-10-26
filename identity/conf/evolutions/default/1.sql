# --- First database schema

# --- !Ups

set ignorecase true;

create table guardian_user(
  id                         varchar(255) not null,
  email                      varchar(255) not null unique,
  eagent                     varchar(255) not null,
  client_id                  varchar(255) not null unique,
  client_secret              varchar(255) not null,
  constraint pk_guardian_user primary key (id));

create sequence guardian_user_seq start with 1000;

insert into guardian_user(id, email, eagent, client_id, client_secret) VALUES (
  'test-id2',
  'theosotr@gmail.com',
  'localhost:9443',
  'dbf21b3a-92e0-44cc-92fc-ba27ca07119e',
  '46f01f9e1ef22a9ccdf0c7ab7f4177ebabe5d58094637c91a8da73e026dfc6b28a4462059dba11fec0c636f9f461508a0637801b482d9e4e68f7ff2a0e257ca4'
);

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists guardian_user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists guardian_user_seq;
