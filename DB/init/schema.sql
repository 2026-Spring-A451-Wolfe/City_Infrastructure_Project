/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: schema.sql                                                        *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Full project database schema, run this file in its entirety in *
                DataGrip to initialize all tables in the correct order.        *
 * Author: Sophina Nichols                                                     *
 * Date Last Modified: 03/05/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

-----------------------------------------------------------------------------
-- DEPARTMENTS
-----------------------------------------------------------------------------
CREATE TABLE departments (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(150)    NOT NULL UNIQUE,
    jurisdiction    VARCHAR(100),
    description     VARCHAR(500),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-----------------------------------------------------------------------------
-- DEPARTMENT CONTACTS 
-----------------------------------------------------------------------------
CREATE TABLE department_contacts (
    id              BIGSERIAL       PRIMARY KEY,
    department_id   BIGINT          NOT NULL 
                        CONSTRAINT department_contacts_departments_id_fk
                            REFERENCES departments(id) ON DELETE CASCADE,
    contact_type    VARCHAR(50)     NOT NULL
                        CONSTRAINT contact_type_selections
                            CHECK (contact_type IN ('phone', 'email', 'website')),
    label           VARCHAR(150),
    value           VARCHAR(255)    NOT NULL,
    is_emergency    BOOLEAN         DEFAULT FALSE
);

-----------------------------------------------------------------------------
-- USERS
-----------------------------------------------------------------------------
CREATE TABLE users (
    id              BIGSERIAL       PRIMARY KEY,
    username        VARCHAR(20)     NOT NULL UNIQUE,
    email_or_phone  VARCHAR(255)    NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    role            VARCHAR(10)     NOT NULL DEFAULT 'Citizen' 
                        CONSTRAINT role_selections
                            CHECK (role IN ('Citizen', 'Admin')),
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    date_created    TIMESTAMP       NOT NULL DEFAULT NOW()
);

-----------------------------------------------------------------------------
-- REPORTS
-----------------------------------------------------------------------------
CREATE TABLE reports (
    id              BIGSERIAL           PRIMARY KEY,
    title           VARCHAR(100)         NOT NULL,
    description     VARCHAR(500)        NOT NULL,
    category        VARCHAR(20)         NOT NULL
                        CONSTRAINT category_selections
                            CHECK (category IN ('Pothole', 'Flooding', 'Streetlight', 'Sign_Damage', 'Road_Damage', 'Debris', 'Other')),
    severity        VARCHAR(10)         NOT NULL
                        CONSTRAINT severity_selections
                            CHECK (severity IN ('Low', 'Medium', 'High', 'Critical')),
    latitude        DOUBLE PRECISION    NOT NULL,
    longitude       DOUBLE PRECISION    NOT NULL,
    status          VARCHAR(15)         NOT NULL DEFAULT 'Requested'
                        CONSTRAINT status_selections
                            CHECK (status IN ('Requested', 'Open', 'In_Progress', 'Resolved', 'Closed', 'Rejected')),
    created_by      BIGINT              NOT NULL
                        CONSTRAINT reports_users_id_fk
                            REFERENCES users (id),
    created_at      TIMESTAMP           NOT NULL DEFAULT NOW(),
    last_update_id  BIGINT,
    updated_at      TIMESTAMP           NOT NULL DEFAULT NOW()
);

-----------------------------------------------------------------------------
-- REPORT UPDATES
-----------------------------------------------------------------------------
CREATE TABLE report_updates (
    id              BIGSERIAL       PRIMARY KEY,
    report_id       BIGINT          NOT NULL
                        CONSTRAINT report_updates_reports_id_fk
                            REFERENCES reports (id) ON DELETE CASCADE,
    updater_id      BIGINT          NOT NULL
                        CONSTRAINT report_updates_users_id_fk
                            REFERENCES users (id),
    old_status      VARCHAR(15)
                        CONSTRAINT old_status_selections
                            CHECK (old_status IN ('Requested', 'Open', 'In_Progress', 'Resolved', 'Closed', 'Rejected')),
    new_status      VARCHAR(15)     NOT NULL
                        CONSTRAINT new_status_selections
                            CHECK (new_status IN ('Requested', 'Open', 'In_Progress', 'Resolved', 'Closed', 'Rejected')),
    department_id   BIGINT
                        CONSTRAINT report_updates_departments_id_fk
                            REFERENCES departments (id),
    comment         VARCHAR(500),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- Add last_update_id foreign key to reports
-- Now that report_updates exists the circular dependency is resolved
ALTER TABLE reports
    ADD CONSTRAINT reports_report_updates_id_fk
        FOREIGN KEY (last_update_id) REFERENCES report_updates(id);

-----------------------------------------------------------------------------
-- REPORT IMAGES
-----------------------------------------------------------------------------
CREATE TABLE report_images (
    id              BIGSERIAL   PRIMARY KEY,
    report_id       BIGINT      NOT NULL
                        CONSTRAINT report_images_reports_id_fk
                            REFERENCES reports (id) ON DELETE CASCADE,
    image_url       VARCHAR(500),
    file_path       VARCHAR(500),
    uploaded_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- INDEXES --
-- Speeds up common queries (map view, filters, dashboards).
