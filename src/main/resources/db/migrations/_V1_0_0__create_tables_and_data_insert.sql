CREATE TABLE "user"
(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    keycloak_id VARCHAR NOT NULL,
    username VARCHAR,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    email VARCHAR NOT NULL,
    phone VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    date_of_birth TIMESTAMP
);

CREATE TABLE file
(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    data BYTEA NOT NULL,
    type VARCHAR NOT NULL,
    date_of_creation TIMESTAMP,
    version BIGINT DEFAULT 1 NOT NULL,
    status VARCHAR NOT NULL,
    note VARCHAR
);

CREATE TABLE file_user
(
    file_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT file_user_pk PRIMARY KEY (file_id, user_id),
    CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES "user" (id),
    CONSTRAINT file_id_fk FOREIGN KEY (file_id) REFERENCES file (id)
);

CREATE TABLE file_version
(
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
    file_id BIGINT NOT NULL,
    name VARCHAR NOT NULL,
    data BYTEA NOT NULL,
    type VARCHAR NOT NULL,
    date_of_creation DATE,
    version BIGINT NOT NULL,
    note VARCHAR,
    CONSTRAINT file_id_fk FOREIGN KEY (file_id) REFERENCES file (id)
);

-- Вставка тестовых данных
INSERT INTO "user" (keycloak_id, username, first_name, last_name, email, phone, password, date_of_birth)
VALUES
('6b44fed2-99c2-453a-8e90-61694dccda49', 'victorkub@gmai.com', 'Victor', 'Kub', 'victorkub@gmai.com', '+375299879871', 'user1', '2004-04-21'),
('e11e4628-1770-4934-bf98-9346296663cd', 'pochta.navsegda@mail.ru', 'Nikita', 'Smolskiy', 'pochta.navsegda@mail.ru', '+375291232341', 'user2', '2000-02-20'),
('68fdcc70-12c4-4013-b159-4c3343ca88b7', 'kiryl.paulau@softclub.by', 'Kirill', 'Pavlov', 'kiryl.paulau@softclub.by', '+375291344191', 'backenddev', '2004-02-26');