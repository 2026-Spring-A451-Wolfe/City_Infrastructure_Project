/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: reports.sql                                                       *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Creates the reports table used to store infrastructure issue.  *
 *              reports submitted by users.                                    *
 * Author: Carter Roberts                                                      *
 * Date Last Modified: 03/03/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

CREATE TABLE reports
(
    id            BIGSERIAL PRIMARY KEY,                    -- identifier for reports
    title          VARCHAR(50) NOT NULL,                    -- title user entered for report
    description    VARCHAR(500) NOT NULL,                   -- description user entered for report
    category       VARCHAR(20) NOT NULL,                    -- category of infrastructure issue user assigned to report
            CONSTRAINT category_selections
                CHECK (category IN ('Pothole', 'Flooding', 'Streetlight', 'Sign_Damage',
                                    'Road_Damage', 'Debris', 'Other')),
    severity       VARCHAR(10) NOT NULL
            CONSTRAINT severity_selections                  -- severity of infrastructure issue user assigned to report
                CHECK (severity IN ('Low', 'Medium', 'High', 'Critical')),
    latitude       DOUBLE PRECISION NOT NULL,               -- latitude coord of report pin placement
    longitude      DOUBLE PRECISION NOT NULL,               -- longitude coord of report pin placement
    status         VARCHAR(15) NOT NULL DEFAULT 'Requested' -- progress status of reported infrastructure issue in accordance with admin review
            CONSTRAINT status_selections
                CHECK (status IN ('Requested', 'Open', 'In_Progress',
                                  'Resolved', 'Closed', 'Rejected')),
    created_by     BIGINT NOT NULL
            CONSTRAINT reports_users_id_fk                  -- foreign key reference to identifier for user who filed report
                REFERENCES users (id),
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),        -- timestamp for when user filed report
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()         -- timestamp for when report is updated
);