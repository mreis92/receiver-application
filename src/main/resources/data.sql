CREATE TABLE IF NOT EXISTS messages
(
    id          bigint  not null,
    content     varchar(255),
    receiver_id integer not null,
    sender_id   varchar(255),
    timestamp   bigint  not null,
    primary key (id)
);