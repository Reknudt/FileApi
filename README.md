## script for docker to run db

```
docker run --name dpspring-pg-16 -p 5432:5432 -e POSTGRES_USER=pguser -e POSTGRES_PASSWORD=pgpass -e POSTGRES_DB=dpspring -d postgres:16
```
```
docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin1 -e KEYCLOAK_ADMIN_PASSWORD=admin1 quay.io/keycloak/keycloak:26.0.5 start-dev
```

## sript for SQL db init:
```
create table "user"
(
id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL PRIMARY KEY,
name VARCHAR NOT NULL,
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
version BIGINT DEFAULT 1 NOT null,
status VARCHAR not null,
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
```
