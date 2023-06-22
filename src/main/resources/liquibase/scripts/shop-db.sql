-- liquibase formatted sql

-- changeset Sergey:1

CREATE TABLE if not exists images
(
    id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    file_path  VARCHAR,
    file_size  BIGINT NOT NULL,
    media_type TEXT   NOT NULL,
    data       oid    NOT NULL
);

-- changeset Yuri:1

CREATE TABLE if not exists users
(
    id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name  VARCHAR NOT NULL,
    phone      VARCHAR NOT NULL,
    password   VARCHAR NOT NULL,
    username   VARCHAR NOT NULL,
    role       VARCHAR DEFAULT 'USER',
    image_id   INTEGER REFERENCES images (id)
);

-- changeset Max:1

CREATE TABLE if not exists ads
(
    id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description TEXT,
    price       INT,
    title       TEXT,
    user_id     INTEGER REFERENCES users (id),
    image_id    INTEGER REFERENCES images (id)
);

CREATE TABLE if not exists comments
(
    id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created_at BIGINT  NOT NULL,
    text       VARCHAR NOT NULL,
    user_id    INTEGER REFERENCES users (id),
    ad_id      INTEGER REFERENCES ads (id)
);

-- changeset Yuri:2
ALTER TABLE images DROP COLUMN file_path;

-- changeset Sergey:2

ALTER TABLE comments ALTER COLUMN created_at TYPE TIMESTAMP USING TIMESTAMP 'epoch' + created_at * INTERVAL '1 millisecond';

