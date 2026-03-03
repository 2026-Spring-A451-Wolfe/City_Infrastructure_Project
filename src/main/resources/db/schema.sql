-- ============================================================
-- schema.sql
--
-- This file creates all the database tables for the NOLA
-- Infrastructure Reporting & Tracking System.
--
-- Tables must be created in this exact order due to foreign
-- key dependencies. Dropping tables must be done in reverse.
--
-- Creation Order:
--   1. departments   (no dependencies)
--   2. users         (no dependencies)
--   3. reports       (depends on users)
--   4. report_updates (depends on reports, users, departments)
--   5. report_images  (depends on reports)
--
-- HOW TO RUN:
--   Open DataGrip, connect to your local nola_db database,
--   open this file and click Run.
-- ============================================================

-- ============================================================
-- 1. DEPARTMENTS
-- Stores the city departments that can be assigned to reports.
-- This table has no foreign key dependencies so it is created first.
-- ============================================================

-- =========================================
-- DEPARTMENTS
-- =========================================
CREATE TABLE departments (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL UNIQUE,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
-- =========================================
-- DEPARTMENT CONTACTS (Multiple phone numbers / websites)
-- =========================================

CREATE TABLE department_contacts (
    id              BIGSERIAL PRIMARY KEY,
    department_id   INTEGER NOT NULL REFERENCES departments(id) ON DELETE CASCADE,
    contact_type    VARCHAR(50) NOT NULL,
    label           VARCHAR(150),
    value           VARCHAR(255) NOT NULL,
    is_emergency    BOOLEAN DEFAULT FALSE
);

-- ============================================================
-- USERS
-- Stores all registered users of the system.
-- Roles: Citizen (submit reports) or Admin (manage reports).
-- ============================================================
CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(20) NOT NULL UNIQUE,
    email           VARCHAR(100) NOT NULL UNIQUE,
    phone           VARCHAR(20) UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(10) NOT NULL DEFAULT 'Citizen' 
            CONSTRAINT role_selections
                CHECK (role IN ('Citizen', 'Admin')),
    is_active         BOOLEAN NOT NULL DEFAULT TRUE,
    date_created      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 3. REPORTS
-- Stores all infrastructure reports submitted by users.
-- last_update_id FK is added after report_updates is created
-- to avoid circular dependency.
-- ============================================================
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
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
);
-- ============================================================
-- 4. REPORT_UPDATES
-- Tracks every status change made to a report.
-- Creates a full history log of who changed what and when.
-- Depends on reports, users, and departments tables.
-- ============================================================
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

-- ============================================================
-- Add last_update_id FK to reports now that report_updates exists
-- ============================================================
ALTER TABLE reports
    ADD CONSTRAINT reports_report_updates_id_fk
        FOREIGN KEY (last_update_id) REFERENCES report_updates(id);


-- ============================================================
-- 5. REPORT_IMAGES
-- Stores images attached to reports.
-- Either image_url (cloud) or file_path (local) will be used.
-- Depends on reports table.
-- ============================================================
CREATE TABLE report_images (
    id              BIGSERIAL PRIMARY KEY,
    report_id       BIGINT NOT NULL
            CONSTRAINT report_images_reports_id_fk
                REFERENCES reports (id) ON DELETE CASCADE,
    image_url       VARCHAR(500),
    file_path       VARCHAR(500),
    uploaded_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- INDEXES
-- Speeds up common queries used by the map view and filters.
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_reports_status
    ON reports(status);

CREATE INDEX IF NOT EXISTS idx_reports_category
    ON reports(category);

CREATE INDEX IF NOT EXISTS idx_reports_created_by
    ON reports(created_by);

CREATE INDEX IF NOT EXISTS idx_reports_location
    ON reports(latitude, longitude);

CREATE INDEX IF NOT EXISTS idx_report_updates_report_id
    ON report_updates(report_id);