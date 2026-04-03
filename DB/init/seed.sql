/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: seed.sql                                                              *
 * Project: NOLA Infrastructure Reporting & Tracking System                        *
 * Description: Inserts initial data into the database, including all New Orleans  *
 *              city departments and their contact information. Also includes      *
 *              commented-out sample users, reports, report updates & images for   *
 *              testing the map view.                                              *
 * Author: Sophina Nichols                                                         *
 * Date Last Modified: 03/14/2026                                                  *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* IMPORTANT!!!
 * Run schema.sql before running this file (Tables must exist before data can be inserted).
 */


/* DEPARTMENTS */
-- Pre-loads all relevant New Orleans city departments.
-- IDs are assigned automatically by BIGSERIAL (1, 2, 3, 4, 5).
INSERT INTO departments (name, jurisdiction, description) VALUES 
(
    'NOLA-311',
    'Orleans Parish',
    'NOLA-311 is the official non-emergency service for New Orleans, allowing residents to report city maintaince issues like potholes, streetlight outages, and drainage issues.'
),
(
    'Department of Public Works (Main Division)',
    'Orleans Parish',
    'The DPW is responsible for maintaining and improving city infrastructure, including streets, sidewalks, streetlights, traffic signals, and street signs. '
),
(
    'Department of Public Works (Maintenance Division)',
    'Orleans Parish',
    'The DPW Maintenance Division is responsible for repairs to potholes and depressions in city streets.'
),
(
    'Department of Public Works (Traffic Division)',
    'Orleans Parish',
    'The DPW Traffic Division is reponsible for installing and repairing street signs, traffic signals, and street lights.'
),
(
    'Sewerage and Water Board of New Orleans',
    'Orleans Parish',
    'The Sewerage and Water Board manages and maintains New Orleans water, sewer, and drainage systems.'
);

/* DEPARTMENT CONTACTS */
-- Pre-loads contact methods for each department.
INSERT INTO department_contacts (department_id, contact_type, label, value, is_emergency) VALUES
(1, 'phone',    'Call NOLA-311',                '311', FALSE),
(1, 'email',    'Email NOLA-311',               '311@nola.gov', FALSE),
(1, 'website',  'NOLA-311 Service Request',     'https://nola311.org', FALSE),

(2, 'phone',    'Call DPW Main Division',         '(504) 658-8000', FALSE),
(2, 'email',    'Email DPW Main Division',        'dpw@nola.gov', FALSE),

(3, 'phone',    'Call DPW Maintenance Division',  '(504) 658-8151', FALSE),
(3, 'phone',    'Call DPW Maintenance Division',  '(504) 658-8152', FALSE),

(4, 'phone',    'Call DPW Traffic Division',      '(504) 658-8040', FALSE),
(4, 'email',    'Email DPW Traffic Division',     'TrafficReviewDPW@nola.gov', FALSE),

(5, 'website',  'Report a Service Request',     'https://www.swbno.org/CustomerService/ReportAnIssue', FALSE),
(5, 'phone',    'Emergency Flood Line',         '504-529-2837', TRUE);

/* USERS */ 
-- Both accounts use password: Test@1234
-- Hashed with BCrypt cost factor 12
INSERT INTO users (username, email_or_phone, password_hash, role) VALUES
('admin_user',   'admin@nola.gov',   '$2a$12$pCkJkNzBqcDLwMbJXBWnHOQfh6Ah4pJ5qJxMBbGhMJyMWkFvJfKHa', 'Admin'),
('citizen_user', 'citizen@nola.gov', '$2a$12$pCkJkNzBqcDLwMbJXBWnHOQfh6Ah4pJ5qJxMBbGhMJyMWkFvJfKHa', 'Citizen');

/* SAMPLE REPORTS */
-- created_by = 2 references citizen_user above.
-- last_update_id is NULL on insert — it will be set once the first
-- report_update is inserted for this report.
-- updated_at auto-populates via DEFAULT NOW().
INSERT INTO reports (title, description, category, severity, latitude, longitude, status, created_by) VALUES
(
    'Large Pothole on Canal Street',
    'Deep pothole approximately 2 feet wide causing damage to vehicles.',
    'Pothole', 'High', 29.9584, -90.0776, 'Open', 2),
(
    'Traffic Light Out on St. Charles',
    'The traffic signal is completely dark on all sides. High collision risk.',
    'Sign_Damage', 'Critical', 29.9531, -90.0700, 'Open', 2),
(
    'Severe Flooding on Broad Street',
    'Water is not draining after the rain, covering the entire right lane.',
    'Flooding', 'High', 29.9678, -90.0883, 'In_Progress', 2),
(
    'Fallen Tree Branch',
    'A large oak tree branch is blocking the sidewalk near Audubon Park.',
    'Debris', 'Low', 29.9329, -90.1332, 'Open', 2),
(
    'Broken Streetlight',
    'Streetlight has been out for a week, making the block very dark and unsafe.',
    'Streetlight', 'Medium', 29.9430, -90.0988, 'Resolved', 2),
(
    'Open Manhole',
    'The cover is completely off. Very dangerous for pedestrians and bikes.',
    'Other', 'High', 29.9602, -90.0811, 'Open', 2);

/* SAMPLE REPORT UPDATES */
-- report_id = 1 references 'Large Pothole on Canal Street' above.
-- updater_id = 1 references admin_user above
-- department_id = 3 references Department of Public Works above
-- updated_at auto-populates via DEFAULT NOW().
INSERT INTO report_updates (report_id, updater_id, old_status, new_status, department_id, comment) VALUES
(
    1, 1, 'Requested', 'In_Progress', 3, 'assigned to department of public works'),
(
    3, 1, 'Open', 'In_Progress', 5, 'Sewerage and Water Board notified of flooding'),
(
    5, 1, 'In_Progress', 'Resolved', 4, 'DPW repaired the streetlight power supply');

-- Update last_update_id for only sample report now that the report update has been made
UPDATE reports
SET last_update_id = 1 WHERE id = 1;
UPDATE reports
SET last_update_id = 2 WHERE id = 3;
UPDATE reports
SET last_update_id = 3 WHERE id = 5;

/* SAMPLE REPORT IMAGES */
-- report_id = 1 references 'Large Pothole on Canal Street] above.
-- uploaded_at auto-populates via DEFAULT NOW().
INSERT INTO report_images (report_id, image_url, file_path) VALUES
(
    1, 'https://cdn.abcteach.com/abcteach-content-free/docs/free_preview/c/chicken01lowres_p.png', 'fakepath/test');

SELECT * FROM departments;
SELECT * FROM department_contacts;
SELECT * FROM users;
SELECT * FROM reports;
SELECT * FROM report_updates;
SELECT * FROM report_images;