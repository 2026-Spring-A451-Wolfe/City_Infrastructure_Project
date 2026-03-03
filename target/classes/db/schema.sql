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
CREATE TABLE IF NOT EXISTS departments (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) UNIQUE NOT NULL,
    description   TEXT,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20) NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);


-- ============================================================
-- 2. USERS
-- Stores all registered users of the system.
-- Roles: Citizen (submit reports) or Admin (manage reports).
-- This table has no foreign key dependencies so it is created second.
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id                SERIAL PRIMARY KEY,
    username          VARCHAR(50) UNIQUE NOT NULL,
    email_or_phonenum VARCHAR(255) UNIQUE NOT NULL,
    password_hash     VARCHAR(255) NOT NULL,
    role              VARCHAR(10) NOT NULL DEFAULT 'Citizen'
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
CREATE TABLE IF NOT EXISTS reports (
    id             BIGSERIAL
        CONSTRAINT reports_pk PRIMARY KEY,
    title          VARCHAR(52) NOT NULL,
    description    VARCHAR(502) NOT NULL,
    category       VARCHAR(13) NOT NULL
        CONSTRAINT category_selections
            CHECK (category IN ('Pothole', 'Flooding', 'Streetlight', 'Sign_Damage', 'Road_Damage', 'Debris', 'Other')),
    severity       VARCHAR(10) NOT NULL
        CONSTRAINT severity_selections
            CHECK (severity IN ('Low', 'Medium', 'High', 'Critical')),
    latitude       DOUBLE PRECISION NOT NULL,
    longitude      DOUBLE PRECISION NOT NULL,
    status         VARCHAR(13) NOT NULL DEFAULT 'Requested'
        CONSTRAINT status_selections
            CHECK (status IN ('Requested', 'Open', 'In_Progress', 'Resolved', 'Closed', 'Rejected')),
    created_by     BIGINT NOT NULL
        CONSTRAINT reports_users_id_fk
            REFERENCES users,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    last_update_id BIGINT
);

COMMENT ON TABLE reports IS 'table for reports that users file';
COMMENT ON COLUMN reports.id IS 'identifier for reports';
COMMENT ON COLUMN reports.title IS 'title user entered for report';
COMMENT ON COLUMN reports.description IS 'description user entered for report';
COMMENT ON COLUMN reports.category IS 'category of infrastructure issue user assigned to report';
COMMENT ON COLUMN reports.severity IS 'severity of infrastructure issue user assigned to report';
COMMENT ON COLUMN reports.latitude IS 'latitude coord of report pin placement';
COMMENT ON COLUMN reports.longitude IS 'longitude coord of report pin placement';
COMMENT ON COLUMN reports.status IS 'progress status of reported infrastructure issue';
COMMENT ON COLUMN reports.created_by IS 'foreign key reference to identifier for user who filed report';
COMMENT ON COLUMN reports.created_at IS 'timestamp for when user filed report';
COMMENT ON COLUMN reports.last_update_id IS 'foreign key reference to identifier for last update to this report';


-- ============================================================
-- 4. REPORT_UPDATES
-- Tracks every status change made to a report.
-- Creates a full history log of who changed what and when.
-- Depends on reports, users, and departments tables.
-- ============================================================
CREATE TABLE IF NOT EXISTS report_updates (
    id            BIGSERIAL
        CONSTRAINT report_updates_pk PRIMARY KEY,
    report_id     BIGINT NOT NULL
        CONSTRAINT report_updates_reports_id_fk
            REFERENCES reports,
    updater_id    BIGINT NOT NULL
        CONSTRAINT report_updates_users_id_fk
            REFERENCES users,
    old_status    VARCHAR(13)
        CONSTRAINT old_status_selections
            CHECK (old_status IN ('Requested', 'Open', 'In_Progress', 'Resolved', 'Closed', 'Rejected')),
    new_status    VARCHAR(13) NOT NULL
        CONSTRAINT new_status_selections
            CHECK (new_status IN ('Requested', 'Open', 'In_Progress', 'Resolved', 'Closed', 'Rejected')),
    department_id BIGINT
        CONSTRAINT report_updates_departments_id_fk
            REFERENCES departments,
    comment       VARCHAR(32),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE report_updates IS 'table for 1:M relationship between a report and the updates it goes through';
COMMENT ON COLUMN report_updates.id IS 'identifier for report update';
COMMENT ON COLUMN report_updates.report_id IS 'foreign key reference to report updated';
COMMENT ON COLUMN report_updates.updater_id IS 'foreign key reference to admin who updated report';
COMMENT ON COLUMN report_updates.old_status IS 'progress status report was in before update';
COMMENT ON COLUMN report_updates.new_status IS 'progress status report is in after update';
COMMENT ON COLUMN report_updates.department_id IS 'foreign key reference to department assigned to report';
COMMENT ON COLUMN report_updates.comment IS 'optional short comment admin added to report update';
COMMENT ON COLUMN report_updates.updated_at IS 'timestamp for when report was updated';


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
CREATE TABLE IF NOT EXISTS report_images (
    id          SERIAL PRIMARY KEY,
    report_id   BIGINT NOT NULL
        REFERENCES reports,
    image_url   VARCHAR(500),
    file_path   VARCHAR(500),
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE report_images IS 'stores images attached to reports';
COMMENT ON COLUMN report_images.report_id IS 'foreign key reference to report this image belongs to';
COMMENT ON COLUMN report_images.image_url IS 'URL if using cloud storage';
COMMENT ON COLUMN report_images.file_path IS 'local path if using filesystem storage';


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