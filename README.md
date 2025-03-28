## script for docker up

```
docker run --name dpspring-pg-16 -p 5432:5432 -e POSTGRES_USER=pguser -e POSTGRES_PASSWORD=pgpass -e POSTGRES_DB=dpspring -d postgres:16
```
## sript for SQL db init:
```
create table "user"
(
id BIGINT generated always as IDENTITY not null PRIMARY KEY,
name VARCHAR NOT NULL,
email VARCHAR NOT NULL,
phone VARCHAR NOT NULL,
password VARCHAR NOT NULL,
date_of_birth DATE
);

CREATE TABLE file
(
id BIGINT generated always as identity NOT null PRIMARY KEY,
name VARCHAR NOT NULL,
data bytea NOT NULL,
type VARCHAR NOT NULL
);

CREATE TABLE file_user
(
file_id bigint NOT NULL,
user_id bigint NOT NULL,
CONSTRAINT file_user_pk PRIMARY KEY (file_id, user_id),
CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES "user" (id),
CONSTRAINT file_id_fk FOREIGN KEY (file_id) REFERENCES file (id)
);
```