/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: report_images.sql                                 *
 * Project: NOLA Infrastructure Reporting & Tracking System    *
 * Description: Stores image attachments for reports.          *
 * Author: Makayla Hairston                                    *
 * Date Last Modified: 03/05/2026                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

CREATE TABLE report_images (
    id          BIGSERIAL PRIMARY KEY,
    report_id   BIGINT NOT NULL
                REFERENCES reports(id) ON DELETE CASCADE,
    image_url   VARCHAR(500),
    file_path   VARCHAR(500),
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW()
);