-- This file populates the database with initial testing data.
-- HOW TO RUN:
--   Run schema.sql first, then run this file in DataGrip.
--
-- TEST LOGIN CREDENTIALS:
--   Admin:   email: admin@nola.gov    password: Test@1234
--   Citizen: email: citizen@nola.gov  password: Test@1234
-- ============================================================


-- ============================================================
-- DEPARTMENTS
-- Pre-loading all New Orleans city departments.
-- ============================================================
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
-- ============================================================
-- DEPARTMENT CONTACTS
-- Pre-loading all New Orleans city department contact info.
-- ============================================================
INSERT INTO department_contacts (department_id, contact_type, label, value, is_emergency) VALUES
(1, 'phone', 'Call NOLA-311', '311'),
(1, 'email', 'Email NOLA-311', '311@nola.gov'),
(1, 'website', 'NOLA-311 Website/Service Request', 'https://nola311.org'),

(2, 'phone', 'Call DPW Main Divion', '(504) 658-8000', FALSE),
(2, 'email', 'Email DPW Main Divion', 'dpw@nola.gov', FALSE),

(3, 'phone', 'Call DPW Maintenance Divion', '(504) 658-8151', FALSE),
(3, 'phone', 'Call DPW Maintenance Divion', '(504) 658-8152', FALSE),

(4, 'phone', 'Call DPW Traffic Divion', '(504) 658-8040', FALSE),
(4, 'email', 'Email DPW Traffic Divion', 'TrafficReviewDPW@nola.gov', FALSE),

(5, 'website', 'Report an Issue/Service Request', 'https://www.swbno.org/CustomerService/ReportAnIssue', FALSE),
(5, 'phone', 'Emergency Flood Line', '504-529-2837', TRUE);

-- ============================================================
-- USERS
-- One test ADMIN and one test CITIZEN account
-- Password for both accounts is: Test@1234
-- Passwords are hashed using BCrypt with cost factor 12
-- ============================================================
--INSERT INTO users (username, email_or_phonenum, password_hash, role) VALUES
--('admin_user',
 --'admin@nola.gov',
 --'$2a$12$pCkJkNzBqcDLwMbJXBWnHOQfh6Ah4pJ5qJxMBbGhMJyMWkFvJfKHa',
 --'ADMIN'),

--('citizen_user',
---- 'citizen@nola.gov',
-- '$2a$12$pCkJkNzBqcDLwMbJXBWnHOQfh6Ah4pJ5qJxMBbGhMJyMWkFvJfKHa',
-- 'CITIZEN');


-- ============================================================
-- SAMPLE REPORTS
-- A mix of categories, severities, and statuses across
-- real New Orleans locations for testing the map view
-- ============================================================
--INSERT INTO reports (title, description, category, severity, latitude, longitude, status, created_by) VALUES

--('Large Pothole on Canal Street',
-- 'Deep pothole approximately 2 feet wide causing damage to vehicles passing through',
 --'POTHOLE', 'HIGH',
 --29.9584, -90.0776,
 --'OPEN', 2),
