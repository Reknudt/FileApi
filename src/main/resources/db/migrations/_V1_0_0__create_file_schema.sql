CREATE TABLE IF NOT EXISTS `files` (    --change name to files

    `id` SERIAL8 NOT NULL,
    `data` BYTEA,
    `name` VARCHAR NOT NULL,
    `type` VARCHAR NOT NULL,
    constraint file_pk PRIMARY KEY (id)

) DEFAULT CHARSET=UTF8;