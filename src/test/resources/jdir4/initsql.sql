CREATE USER testrest WITH PASSWORD 'secret';
create database testrest with owner testrest;

create table test (x int);
delete from test;
insert into test values(1);
insert into test values(3);
insert into test values(3);
insert into test values(2);

create table testm (name varchar(100));
delete from testm

create table testnum(id  character varying(6),  num numeric(20,2));
insert into testnum values('AA',123.56);
