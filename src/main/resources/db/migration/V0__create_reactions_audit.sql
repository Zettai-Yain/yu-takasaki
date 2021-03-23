CREATE TABLE reactions_audit
(
    seq       bigserial,
    timestamp timestamp with time zone,
    type      int,
    voter     bigint,
    voted     bigint,
    reaction  varchar,

    PRIMARY KEY (seq)
);
