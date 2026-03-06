/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: report_images.sql                                 *
 * Project: NOLA Infrastructure Reporting & Tracking System    *
 * Description: Stores image attachments for reports.          *
 * Author: Makayla Hairston                                    *
 * Date Last Modified: 03/05/2026                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

CREATE TABLE report_images (
    id          integer PRIMARY KEY,
    report_id   integer NOT NULL
                REFERENCES reports(id)
    image_url   TEXT,
    file_path   TEXT,
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW()
);
