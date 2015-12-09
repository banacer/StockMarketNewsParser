create table keygroup
(
  gr_ID int primary key,
  source int references keyword on update cascade on delete set null,
  gr_grade double,
  gr_num int,
  gr_sd double
)

create table keyword
(
  key_ID int primary key,
  key_grade double,
  key_num int,
  key_sd double,
  gr_ID int references keygroup(gr_ID) on update cascade on delete set null
)

create table word
(
  key_ID int primary key references keyword(key_ID) on update cascade on delete cascade,
  content varchar(200)
)

create table entity
(
  key_ID int primary key references keyword(key_ID) on update cascade on delete cascade,
  en_name varchar(200),
  en_description varchar(200),
  en_human int
)

create table source
(
  src_ID int primary key,
  src_link varchar(1000),
  src_name varchar(200),
  src_credib double
)

create table article
(
  ar_ID int primary key auto_increment,
  src_ID int references source(src_ID) on update cascade on delete set null,
  ar_link varchar(1000),
  downloaded int,
  analyzed int
)

create table credibility
(
  cr_estimate double,
  cr_real double,
  date datetime
)

create table alias 
(
  key_id int references entity(key_id) on update cascade on delete cascade,
  content varchar(200),
  primary key(key_id,content)
)