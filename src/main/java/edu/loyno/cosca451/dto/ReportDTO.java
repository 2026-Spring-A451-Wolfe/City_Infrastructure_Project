/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportDTO.java                                                    *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Data Transfer Object for sending report data to the client.    *
 *              Used as the response payload for report endpoints. Should      *
 *              include a nested list of ReportUpdateDTOs for status history   *
 *              and a nested list of image URLs/paths when applicable.         *
 * Author: Adin Hultin                                                         *
 * Date Last Modified: 03/05/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReportDTO {

    private long id;
    private String title;
    private String description;
    private String category;
    private String severity;
    private double latitude;
    private double longitude;
    private String status;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReportUpdateDTO> updates;
    private List<ReportImageDTO> images;

    public ReportDTO() {}

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

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<ReportUpdateDTO> getUpdates() { return updates; }
    public void setUpdates(List<ReportUpdateDTO> updates) { this.updates = updates; }

    public List<ReportImageDTO> getImages() { return images; }
    public void setImages(List<ReportImageDTO> images) { this.images = images; }
}
