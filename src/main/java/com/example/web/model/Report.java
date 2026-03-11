/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: Report.java                                                     *
 * Project: NOLA Infrastructure Reporting & Tracking System                  *
 * Description: Represents an infrastructure report submitted by a citizen,  *
 *              mapped to the reports table.                                 *
 * Author: Adin Hultin                                                       *
 * Date Last Modified: 03/05/2026                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.model;

import java.time.LocalDateTime;

public class Report {

    private long id;
    private String title;
    private String description;
    private String category;
    private String severity;
    private double latitude;
    private double longitude;
    private String status;
    private long createdBy;
    private LocalDateTime createdAt;
    private Long lastUpdateId;
    private LocalDateTime updatedAt;

    public Report() {}

    public Report(String title, String description, String category,
                  String severity, double latitude, double longitude, long createdBy) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.severity = severity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdBy = createdBy;
        this.status = "Requested";
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedBy() { return createdBy; }
    public void setCreatedBy(long createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getLastUpdateId() { return lastUpdateId; }
    public void setLastUpdateId(Long lastUpdateId) { this.lastUpdateId = lastUpdateId; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
