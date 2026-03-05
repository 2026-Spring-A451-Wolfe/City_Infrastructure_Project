/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: users.sql                                               *
 * Project: NOLA Infrastructure Reporting & Tracking System          *
 * Description: Creates the users table, storing registered users.   *
                Admin accounts must be created directly in the DB.   *
 * Author: Makayla Hairston                                          *
 * Date Last Modified: 03/05/2026                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

CREATE TABLE users (
    id                  BIGSERIAL PRIMARY KEY,
    username            VARCHAR(20) NOT NULL UNIQUE,
    email_or_phone      VARCHAR(255) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    role                VARCHAR(10) NOT NULL DEFAULT 'Citizen'
                                    CHECK (role IN ('Citizen', 'Admin')),
    is_active           BOOLEAN     NOT NULL DEFAULT TRUE,
    date_created        TIMESTAMP   NOT NULL DEFAULT NOW()
);