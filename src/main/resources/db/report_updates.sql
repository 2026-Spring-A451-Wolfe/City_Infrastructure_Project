create table report_updates
(
    id            bigserial
        constraint report_updates_pk
            primary key,
    report_id     bigint                  not null,
    updater_id    bigint                  not null
        constraint report_updates_users_id_fk
            references users,
    old_status    varchar(13)
        constraint old_status_selections
            check ((old_status)::text = ANY
                   ((ARRAY ['Requested'::character varying, 'Open'::character varying, 'In_Progress'::character varying, 'Resolved'::character varying, 'Closed'::character varying, 'Rejected'::character varying])::text[])),
    new_status    varchar(13)             not null
        constraint new_status_selections
            check ((new_status)::text = ANY
                   ((ARRAY ['Requested'::character varying, 'Open'::character varying, 'In_Progress'::character varying, 'Resolved'::character varying, 'Closed'::character varying, 'Rejected'::character varying])::text[])),
    department_id bigint
        constraint report_updates_departments_id_fk
            references departments,
    comment       varchar(32),
    updated_at    timestamp default now() not null
);

comment on table report_updates is 'table for 1:M relationship between a report and the updates it goes through';

comment on column report_updates.id is 'identifier for report update';

comment on column report_updates.report_id is 'foreign key reference to report updated';

comment on column report_updates.updater_id is 'foreign key reference to admin who updated report';

comment on constraint report_updates_users_id_fk on report_updates is 'links "updater_id" from report_updates to "id" from users';

comment on column report_updates.old_status is 'progress status report was in before update, can be null if new status is Requested';

comment on column report_updates.new_status is 'progress status report is in after update';

comment on column report_updates.department_id is 'foreign key reference to, if department assigned to report, which department (can be null if not assigned yet)';

comment on constraint report_updates_departments_id_fk on report_updates is 'links "department_id" from report_updates to "id" from departments';

comment on column report_updates.comment is 'optional (thus can be null) short comment admin added to report update';

comment on column report_updates.updated_at is 'Timestamp for when report was updated';