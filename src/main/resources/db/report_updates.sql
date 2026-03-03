/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: report_updates.sql                                                *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Creates the report_updates table used to track every status    *
 *              change made to a report, maintaining a full audit/history log. *
 * Author: Carter Roberts                                                      *
 * Date Last Modified: 03/03/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

CREATE TABLE report_updates
(
    id              BIGSERIAL PRIMARY KEY,                -- identifier for report update
    report_id       BIGINT NOT NULL,                      -- foreign key reference to report updated
            CONSTRAINT report_updates_reports_id_fk
                REFERENCES reports (id) ON DELETE CASCADE,
    updater_id      BIGINT NOT NULL,                      -- foreign key reference to admin who updated report
            CONSTRAINT report_updates_users_id_fk         -- links "updater_id" from report_updates to "id" from users
                REFERENCES users (id),
    old_status      VARCHAR(15),                          -- progress status report was in before update, can be null if new status is Requested
            CONSTRAINT old_status_selections
                CHECK (old_status IN ('Requested', 'Open', 'In_Progress',
                                      'Resolved', 'Closed', 'Rejected')),
    new_status      VARCHAR(15) NOT NULL                  -- progress status report is in after update
            CONSTRAINT new_status_selections
                CHECK (new_status IN ('Requested', 'Open', 'In_Progress',
                                      'Resolved', 'Closed', 'Rejected')),
    department_id   BIGINT,                               -- foreign key reference to, if department assigned to report, which department
            CONSTRAINT report_updates_departments_id_fk   -- links "department_id" from report_updates to "id" from departments
                REFERENCES departments (id),
    comment         VARCHAR(255),                          -- optional (thus can be null) short comment admin added to report update
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()     -- Timestamp for when report was updated'
);

--Before--
--create table report_updates
--(
  --  id            bigserial
    --    constraint report_updates_pk
      --      primary key,
    --report_id     bigint                  not null,
    --updater_id    bigint                  not null
      --  constraint report_updates_users_id_fk
        --    references users,
    --old_status    varchar(13)
      --  constraint old_status_selections
        --    check ((old_status)::text = ANY
          --         ((ARRAY ['Requested'::character varying, 'Open'::character varying, 'In_Progress'::character varying, 'Resolved'::character varying, 'Closed'::character varying, 'Rejected'::character varying])::text[])),
    --new_status    varchar(13)             not null
      --  constraint new_status_selections
        --    check ((new_status)::text = ANY
          --         ((ARRAY ['Requested'::character varying, 'Open'::character varying, 'In_Progress'::character varying, 'Resolved'::character varying, 'Closed'::character varying, 'Rejected'::character varying])::text[])),
    --department_id bigint
      --  constraint report_updates_departments_id_fk
        --    references departments,
    --comment       varchar(32),
    --updated_at    timestamp default now() not null
--);