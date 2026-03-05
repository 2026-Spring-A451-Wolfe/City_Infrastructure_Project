------------------------------------------------------------------
-- Filename: schema.sql
-- Project: Infrastructure Reporting & Tracking System
-- Description: Creates and defines all project databse tables.
-- Author: Sophina Nichols
-- Date Last Modified: 03/04/2026
------------------------------------------------------------------

-- DEPARTMENTS --
-- Stores city departments that can be assigned to reports.
CREATE TABLE departments (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL UNIQUE,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- DEPARTMENT CONTACTS --
-- Stores department contact information.
-- Allows multiple contact methods per department.
CREATE TABLE department_contacts (
    id              BIGSERIAL PRIMARY KEY,
    department_id   INTEGER NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    contact_type    VARCHAR(50) NOT NULL,
    label           VARCHAR(150),
    value           VARCHAR(255) NOT NULL,
    is_emergency    BOOLEAN DEFAULT FALSE
);

-- USERS --
-- Stores all registered users and assigns roles.
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(20) NOT NULL UNIQUE,
    email_or_phone  VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(10) NOT NULL DEFAULT 'Citizen' 
            CONSTRAINT role_selections
                CHECK (role IN ('Citizen', 'Admin')),
    is_active         BOOLEAN NOT NULL DEFAULT TRUE,
    date_created      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- REPORTS -- 
-- Stores infrastructure reports submitted by users.
-- last_update_id foreign key's constraint is added after report_updates table is created 
-- to avoid circular dependency.
CREATE TABLE reports (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(50) NOT NULL,
    description     VARCHAR(500) NOT NULL,
    category        VARCHAR(20) NOT NULL
            CONSTRAINT category_selections
                    CHECK (category IN ('Pothole', 'Flooding', 'Streetlight', 'Sign_Damage', 'Road_Damage', 'Debris', 'Other')),
    severity        VARCHAR(10) NOT NULL
            CONSTRAINT severity_selections
                CHECK (severity IN ('Low', 'Medium', 'High', 'Critical')),
    latitude        DOUBLE PRECISION NOT NULL,
    longitude       DOUBLE PRECISION NOT NULL,
    status          VARCHAR(15) NOT NULL DEFAULT 'Requested'
            CONSTRAINT status_selections
                CHECK (status IN ('Requested', 'Open', 'In_Progress', 'Resolved', 'Closed', 'Rejected')),
    created_by      BIGINT NOT NULL
            CONSTRAINT reports_users_id_fk
                REFERENCES users (id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    last_update_id  BIGINT NOT NULL,
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
);

-- REPORT UPDATES --
-- Tracks every status change made to a report.
-- Maintains a compelte audit/history log.
CREATE TABLE report_updates (
    id              BIGSERIAL PRIMARY KEY
    report_id       BIGINT NOT NULL
            CONSTRAINT report_updates_reports_id_fk
                REFERENCES reports (id) ON DELETE CASCADE,
    updater_id      BIGINT NOT NULL
            CONSTRAINT report_updates_users_id_fk
                REFERENCES users (id),
    old_status      VARCHAR(15)
            CONSTRAINT old_status_selections
                CHECK (old_status IN ('Requested', 'Open', 'In_Progress', 'Resolved', 'Closed', 'Rejected')),
    new_status      VARCHAR(15) NOT NULL
            CONSTRAINT new_status_selections
                CHECK (new_status IN ('Requested', 'Open', 'In_Progress', 'Resolved', 'Closed', 'Rejected')),
    department_id   BIGINT
            CONSTRAINT report_updates_departments_id_fk
                REFERENCES departments (id),
    comment         VARCHAR(32),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Add last_update_id foreign key to reports
ALTER TABLE reports
    ADD CONSTRAINT reports_report_updates_id_fk
        FOREIGN KEY (last_update_id) REFERENCES report_updates(id);

-- REPORT IMAGES --
-- Stores images attached to reports.
-- Either image_url (cloud) or file_path (local) may be used.
CREATE TABLE report_images (
    id              BIGSERIAL PRIMARY KEY,
    report_id       BIGINT NOT NULL
            CONSTRAINT report_images_reports_id_fk
                REFERENCES reports (id) ON DELETE CASCADE,
    image_url       VARCHAR(500),
    file_path       VARCHAR(500),
    uploaded_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- INDEXES --
-- Speeds up common queries (map view, filters, dashboards).
