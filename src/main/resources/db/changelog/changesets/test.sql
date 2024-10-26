--liquibase formatted sql

--changeset joukl:1
create table TEST
(
    TEST_ID int not null
        primary key
);

--changeset joukl:2
create table TEST2
(
    TEST_ID int not null
        primary key
);

--changeset joukl:3
CREATE TABLE TESTICEK (
    testicekId INT AUTO_INCREMENT PRIMARY KEY
);