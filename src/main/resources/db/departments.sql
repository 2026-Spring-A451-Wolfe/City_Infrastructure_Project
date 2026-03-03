CREATE TABLE departments (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL UNIQUE,
    jurisdiction    VARCHAR(100),
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);