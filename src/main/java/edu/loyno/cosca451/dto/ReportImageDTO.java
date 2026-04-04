/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportUpdateDTO.java                                                *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: Data Transfer Object for sending image attachment data to the    *
 *              client. Used as the response payload for image-related endpoints *
 *              and as a nested object inside ReportDTO. Contains id, reportId,  *
 *              imageUrl, filePath, and uploadedAt. Never expose raw file system *
 *              paths to unauthorized users.                                     *
 * Author: Makayla Hairston                                                      *
 * Edited By:                                                                    *
 * Hector Maes - 04/02/2026                                                      *
 * Date Last Modified: 04/02/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.dto;

import java.time.LocalDateTime;

/* ReportImageDTO is a DTO that represents image attachment data sent back
 * to the client. It is used as the response payload for image-related
 * endpoints and may also be included as a nested object inside ReportDTO.
 */

public class ReportImageDTO {

    private long id;
    private long reportId;
    private String imageUrl;
    private String filePath;
    private LocalDateTime uploadedAt;

    public ReportImageDTO() {}

    public ReportImageDTO(long id, long reportId, String imageUrl, String filePath,
                          LocalDateTime uploadedAt) {
        this.id = id;
        this.reportId = reportId;
        this.imageUrl = imageUrl;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getReportId() { return reportId; }
    public void setReportId(long reportId) { this.reportId = reportId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
