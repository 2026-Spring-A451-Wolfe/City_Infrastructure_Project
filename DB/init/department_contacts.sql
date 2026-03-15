/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: department_contacts.sql                                             *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: Creates the department_contacts table, allows multiple contact   *
 *              methods (phone, email, website) per department.                  *
 * Author: Sophina Nichols                                                       *
 * Date Last Modified: 03/03/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

CREATE TABLE department_contacts (
    id              BIGSERIAL PRIMARY KEY,
    department_id   BIGINT NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    contact_type    VARCHAR(50) NOT NULL, 
    label           VARCHAR(150),
    value           VARCHAR(255) NOT NULL,
    is_emergency    BOOLEAN DEFAULT FALSE
);