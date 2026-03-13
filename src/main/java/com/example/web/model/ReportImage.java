/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportImage.java                                                  *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Represents an image attachment linked to a report, mapped to   *
 *              the report_images table. Supports both cloud-hosted URLs and   *
 *              local server file paths.                                       *
 * Author: Adin Hultin                                                         *
 * Date Last Modified: 03/05/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.model;

import java.time.LocalDateTime;

public class ReportImage {

    private long id;
    private long reportId;
    private String imageUrl;
    private String filePath;
    private LocalDateTime uploadedAt;
    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private long fileSize;

    public ReportImage() {}

    public ReportImage(long reportId, String imageUrl, String filePath) {
        this.reportId = reportId;
        this.imageUrl = imageUrl;
        this.filePath = filePath;
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

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getStoredFilename() { return storedFilename; }
    public void setStoredFilename(String storedFilename) { this.storedFilename = storedFilename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
}
