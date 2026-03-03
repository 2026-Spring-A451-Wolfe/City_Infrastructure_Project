/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: departments.sql                                                 *
 * Project: NOLA Infrastructure Reporting & Tracking System                  *
 * Description: Creates and defines the structure of the departments table   *
 *              used to store city department records.                       *
 * Author: Sophina Nichols                                                   *
 * Date Last Modified: 03/03/2026                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

CREATE TABLE departments (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL UNIQUE,
    jurisdiction    VARCHAR(100),
    description     VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);