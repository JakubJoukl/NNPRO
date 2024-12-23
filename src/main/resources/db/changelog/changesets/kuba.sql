--liquibase formatted sql

-- changeset Kuba:1729957651903-1
CREATE TABLE conversation (conversation_id INT NOT NULL, CONSTRAINT conversationPK PRIMARY KEY (conversation_id));

CREATE TABLE conversation_user (conversation_id INT NOT NULL, user_id INT NOT NULL, CONSTRAINT conversation_userPK PRIMARY KEY (conversation_id, user_id));

CREATE TABLE message (message_id INT AUTO_INCREMENT NOT NULL, receiver_id INT NOT NULL, sender_id INT NOT NULL, CONSTRAINT messagePK PRIMARY KEY (message_id));

CREATE TABLE public_key (public_key_id INT AUTO_INCREMENT NOT NULL, creation_date datetime(6) NULL, fingerprint VARCHAR(255) NULL, `key` VARCHAR(255) NULL, valid BIT NULL, owner_id INT NOT NULL, CONSTRAINT public_keyPK PRIMARY KEY (public_key_id));

CREATE TABLE reset_token (reset_token_id INT AUTO_INCREMENT NOT NULL, expiration_date datetime(6) NULL, token VARCHAR(255) NULL, valid BIT NULL, user_user_id INT NULL, CONSTRAINT reset_tokenPK PRIMARY KEY (reset_token_id));

CREATE TABLE user (user_id INT AUTO_INCREMENT NOT NULL, email VARCHAR(255) NULL, password VARCHAR(255) NULL, salt VARCHAR(255) NULL, username VARCHAR(255) NULL, CONSTRAINT userPK PRIMARY KEY (user_id));

CREATE TABLE user_contact (user_id INT NOT NULL, contact_user_id INT NOT NULL);

ALTER TABLE reset_token ADD CONSTRAINT FK6himxjdq7jb374984mt2w47cn FOREIGN KEY (user_user_id) REFERENCES user (user_id);

ALTER TABLE message ADD CONSTRAINT FK86f0kc2mt26ifwupnivu6v8oa FOREIGN KEY (receiver_id) REFERENCES user (user_id);

ALTER TABLE user_contact ADD CONSTRAINT FK9fm1vrfyjcs735xlykhnedyd7 FOREIGN KEY (user_id) REFERENCES user (user_id);

ALTER TABLE conversation_user ADD CONSTRAINT FKb71b5q60yd0bfc1eb8fgwm4sk FOREIGN KEY (conversation_id) REFERENCES conversation (conversation_id);

ALTER TABLE message ADD CONSTRAINT FKcnj2qaf5yc36v2f90jw2ipl9b FOREIGN KEY (sender_id) REFERENCES user (user_id);

ALTER TABLE public_key ADD CONSTRAINT FKh0vu8klgwmqjfys2jb69vqx9h FOREIGN KEY (owner_id) REFERENCES user (user_id);

ALTER TABLE conversation_user ADD CONSTRAINT FKhjie8c93f6ctc27ujqg84lx0f FOREIGN KEY (user_id) REFERENCES user (user_id);

ALTER TABLE user_contact ADD CONSTRAINT FKw0i4lv7jigx9xt7iq6gyxp4s FOREIGN KEY (contact_user_id) REFERENCES user (user_id);

-- changeset Kuba:1730060028685-1
CREATE TABLE verification_code (verification_code_id INT NOT NULL, expiration_date datetime(6) NULL, valid BIT NULL, verification_code VARCHAR(255) NULL, user_id INT NOT NULL, CONSTRAINT verification_codePK PRIMARY KEY (verification_code_id));

ALTER TABLE verification_code ADD CONSTRAINT FKgy5dhio3a6c9me7s0x9v1y4d2 FOREIGN KEY (user_id) REFERENCES user (user_id);

-- changeset Kuba:1730115537739-1
ALTER TABLE conversation ADD conversation_name VARCHAR(255) NOT NULL;

-- changeset Kuba:1730317689332-6
ALTER TABLE message DROP FOREIGN KEY FK86f0kc2mt26ifwupnivu6v8oa;

ALTER TABLE message ADD content VARCHAR(4000) NULL;

ALTER TABLE message ADD conversation_id INT NOT NULL;

ALTER TABLE message ADD date_send datetime(6) NOT NULL;

ALTER TABLE conversation_user ADD is_active BIT NOT NULL;

ALTER TABLE message ADD CONSTRAINT FK6yskk3hxw5sklwgi25y6d5u1l FOREIGN KEY (conversation_id) REFERENCES conversation (conversation_id);

ALTER TABLE message DROP COLUMN receiver_id;

ALTER TABLE user DROP COLUMN salt;

-- changeset Kuba:1730317689332-7

ALTER TABLE conversation_user DROP FOREIGN KEY FKb71b5q60yd0bfc1eb8fgwm4sk;

ALTER TABLE message DROP FOREIGN KEY FK6yskk3hxw5sklwgi25y6d5u1l;

ALTER TABLE verification_code MODIFY verification_code_id int NOT NULL AUTO_INCREMENT;

ALTER TABLE conversation MODIFY conversation_id int NOT NULL AUTO_INCREMENT;

ALTER TABLE conversation_user ADD CONSTRAINT FKb71b5q60yd0bfc1eb8fgwm4sk FOREIGN KEY (conversation_id) REFERENCES conversation (conversation_id);

ALTER TABLE message ADD CONSTRAINT FK6yskk3hxw5sklwgi25y6d5u1l FOREIGN KEY (conversation_id) REFERENCES conversation (conversation_id);

-- changeset Kuba:1730919867185-1
ALTER TABLE public_key DROP COLUMN fingerprint;

-- changeset Kuba:1731351661524-1
ALTER TABLE public_key ADD key_value VARCHAR(255) NOT NULL;

-- changeset Kuba:1731351661524-2
ALTER TABLE public_key DROP COLUMN `key`;

-- changeset Kuba:1731766141949-1
ALTER TABLE conversation_user ADD cyphered_symmetric_key VARCHAR(255) NULL;

-- changeset Kuba:1731779873272-1
ALTER TABLE conversation_user ADD ciphering_public_key VARCHAR(4000) NULL;

-- changeset Kuba:1731779873272-2
ALTER TABLE conversation_user ADD encrypted_symmetric_key VARCHAR(255) NULL;

-- changeset Kuba:1731779873272-3
ALTER TABLE conversation_user DROP COLUMN cyphered_symmetric_key;

-- changeset Kuba:1731854773482-1
ALTER TABLE conversation_user ADD initiation_vector VARCHAR(400) NULL;

-- changeset Kuba:1731864337621-1
ALTER TABLE message ADD valid_to datetime(6);

-- changeset Kuba:1732049833344-1
ALTER TABLE message ADD initiation_vector VARCHAR(400) NULL;

-- changeset Kuba:1732131563129-1
ALTER TABLE message MODIFY content VARCHAR(10000);

-- changeset Kuba:1733951131754-1
ALTER TABLE user ADD CONSTRAINT UC_USEREMAIL_COL UNIQUE (email);

-- changeset Kuba:1733951131754-2
ALTER TABLE user ADD CONSTRAINT UC_USERUSERNAME_COL UNIQUE (username);

-- changeset Kuba:1734470421915-1
ALTER TABLE conversation_user ADD encrypted_symmetric_key_added_on datetime(6) DEFAULT CURRENT_TIMESTAMP(6) NOT NULL;

-- changeset Kuba:1734959123530-1
CREATE TABLE authority (authority_id INT AUTO_INCREMENT NOT NULL, authority_name VARCHAR(255) NULL, CONSTRAINT authorityPK PRIMARY KEY (authority_id));

-- changeset Kuba:1734959123530-2
CREATE TABLE user_authorities (authority_id INT NOT NULL, user_id INT NOT NULL);

-- changeset Kuba:1734959123530-3
ALTER TABLE authority ADD CONSTRAINT UC_AUTHORITYAUTHORITY_NAME_COL UNIQUE (authority_name);

-- changeset Kuba:1734959123530-4
ALTER TABLE user_authorities ADD CONSTRAINT FK2n9bab2v62l3y2jgu3qup4etw FOREIGN KEY (authority_id) REFERENCES authority (authority_id);

-- changeset Kuba:1734959123530-5
ALTER TABLE user_authorities ADD CONSTRAINT FKmj13d0mnuj4cd8b6htotbf9mm FOREIGN KEY (user_id) REFERENCES user (user_id);

-- changeset Kuba:1734959123530-6
INSERT INTO authority (authority_name) values ('USER');

-- changeset Kuba:1734959123530-7
INSERT INTO authority (authority_name) values ('ADMIN');

-- changeset Kuba:1734965094388-1
ALTER TABLE user ADD banned BIT NULL;

-- changeset Kuba:1734978194681-1
CREATE TABLE auth_token (auth_token_id INT AUTO_INCREMENT NOT NULL, jwt_hash VARCHAR(255) NULL, valid BIT NULL, user_user_id INT NULL, CONSTRAINT auth_tokenPK PRIMARY KEY (auth_token_id));

-- changeset Kuba:1734978194681-2
ALTER TABLE auth_token ADD CONSTRAINT FKere73v3jning5og3pv82shkfb FOREIGN KEY (user_user_id) REFERENCES user (user_id);