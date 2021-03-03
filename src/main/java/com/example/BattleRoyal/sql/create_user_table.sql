create table if not exists user_table
(
    id integer auto_increment not null,
    level integer,
    user_name varchar (30),
    password varchar (50),
    email varchar (50),
    PRIMARY KEY(id)
);

alter table battle_royal.user_table modify column email varchar(50) NOT NULL unique
