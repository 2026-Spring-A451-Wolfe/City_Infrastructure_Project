CREATE TABLE department_contacts (
    id              BIGSERIAL PRIMARY KEY,
    department_id   INTEGER NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    contact_type    VARCHAR(50) NOT NULL, 
    label           VARCHAR(150),
    value           VARCHAR(255) NOT NULL,
    is_emergency    BOOLEAN DEFAULT FALSE
);