CREATE TABLE IF NOT EXISTS messages
(
    id          identity not null primary key,
    content     varchar(255),
    receiver_id integer  not null,
    sender_id   varchar(255),
    timestamp   bigint   not null
);