create table reports
(
    id             bigserial
        constraint reports_pk
            primary key,
    title          varchar(52)                                        not null,
    description    varchar(502)                                       not null,
    category       varchar(13)                                        not null
        constraint category_selections
            check ((category)::text = ANY
                   ((ARRAY ['Pothole'::character varying, 'Flooding'::character varying, 'Streetlight'::character varying, 'Sign_Damage'::character varying, 'Road_Damage'::character varying, 'Debris'::character varying, 'Other'::character varying])::text[])),
    severity       varchar(10)                                        not null
        constraint severity_selections
            check ((severity)::text = ANY
                   ((ARRAY ['Low'::character varying, 'Medium'::character varying, 'High'::character varying, 'CRITICAL'::character varying])::text[])),
    latitude       double precision                                   not null,
    longitude      double precision                                   not null,
    status         varchar(13) default 'Requested'::character varying not null
        constraint status_selections
            check ((status)::text = ANY
                   ((ARRAY ['Requested'::character varying, 'Open'::character varying, 'In_Progress'::character varying, 'Resolved'::character varying, 'Closed'::character varying, 'Rejected'::character varying])::text[])),
    created_by     bigint                                             not null
        constraint reports_users_id_fk
            references users,
    created_at     timestamp   default now()                          not null,
    last_update_id bigint
        constraint reports_report_updates_id_fk
            references report_updates
);

comment on table reports is 'table for reports that users file';

comment on column reports.id is 'identifier for reports';

comment on column reports.title is 'title user entered for report';

comment on column reports.description is 'description user entered for report';

comment on column reports.category is 'category of infrastructure issue user assigned to report';

comment on column reports.severity is 'severity of infrastructure issue user assigned to report';

comment on column reports.latitude is 'latitude coord of report pin placement';

comment on column reports.longitude is 'longitude coord of report pin placement';

comment on column reports.status is 'progress status of reported infrastructure issue in accordance with admin review';

comment on column reports.created_by is 'foreign key reference to identifier for user who filed report';

comment on column reports.created_at is 'timestamp for when user filed report';

comment on column reports.last_update_id is 'foreign key reference to identifier for last update to this report, may be null if still unreviewed/requested';
