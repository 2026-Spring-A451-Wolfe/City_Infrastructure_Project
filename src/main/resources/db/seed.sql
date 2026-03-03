------------------------------------------------------------------
-- Filename: seed.sql
-- Project: Infrastructure Reporting & Tracking System
-- Description: Inserts initial data into database tables, including
--              departments and their contact information.
-- Author: Sophina Nichols
-- Date Last Modified: 03/03/2026
------------------------------------------------------------------

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: seed.sql                                                              *
 * Project: NOLA Infrastructure Reporting & Tracking System                        *
 * Description: Inserts initial data into the database, including all New Orleans  *
 *              city departments and their contact information. Also includes      *
 *              commented-out sample users and reports for testing the map view.   *
 * Author: Sophina Nichols                                                         *
 * Date Last Modified: 03/03/2026                                                  *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* IMPORTANT!!!
 * Run schema.sql before running this file.
 * Tables must exist before data can be inserted.
 */


/* DEPARTMENTS */
-- Pre-loads all relevant New Orleans city departments.
-- IDs are assigned automatically by BIGSERIAL (1, 2, 3, 4, 5).
INSERT INTO departments (name, jurisdiction, description) VALUES 
(
    'NOLA-311',
    'Orleans Parish',
    'NOLA-311 is the official non-emergency service for New Orleans, allowing residents to report city maintaince issues
     like potholes, streetlight outages, and drainage issues.'
),
(
    'Department of Public Works (Main Division)',
    'Orleans Parish',
    'The DPW is responsible for maintaining and improving city infrastructure, including
     streets, sidewalks, streetlights, traffic signals, and street signs. '
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
(1, 'phone', 'Call NOLA-311', '311'),
(1, 'email', 'Email NOLA-311', '311@nola.gov'),
(1, 'website', 'NOLA-311 Website/Service Request', 'https://nola311.org'),

(2, 'phone', 'Call DPW Main Divion', '(504) 658-8000'),
(2, 'email', 'Email DPW Main Divion', 'dpw@nola.gov'),

(3, 'phone', 'Call DPW Maintenance Divion', '(504) 658-8151'),
(3, 'phone', 'Call DPW Maintenance Divion', '(504) 658-8152'),

(4, 'phone', 'Call DPW Traffic Divion', '(504) 658-8040'),
(4, 'email', 'Email DPW Traffic Divion', 'TrafficReviewDPW@nola.gov'),

(5, 'website', 'Report an Issue/Service Request', 'https://www.swbno.org/CustomerService/ReportAnIssue'),
(5, 'phone', 'Emergency Flood Line', '504-529-2837', TRUE);

/* USERS */ 
-- Both accounts use password: Test@1234
-- Hashed with BCrypt cost factor 12
INSERT INTO users (username, email, phone, password_hash, role) VALUES
('admin_user',   'admin@nola.gov',   '$2a$12$pCkJkNzBqcDLwMbJXBWnHOQfh6Ah4pJ5qJxMBbGhMJyMWkFvJfKHa', 'Admin'),
('citizen_user', 'citizen@nola.gov', '$2a$12$pCkJkNzBqcDLwMbJXBWnHOQfh6Ah4pJ5qJxMBbGhMJyMWkFvJfKHa', 'Citizen');

/* SAMPLE REPORTS */ 
INSERT INTO reports (title, description, category, severity, latitude, longitude, status, created_by) VALUES
('Large Pothole on Canal Street',
'Deep pothole approximately 2 feet wide causing damage to vehicles.',
'Pothole', 'High', 29.9584, -90.0776, 'Open', 2);
