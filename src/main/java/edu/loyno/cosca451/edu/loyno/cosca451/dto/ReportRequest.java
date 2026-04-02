/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportRequest.java                                                *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Inbound request payload for submitting a new infrastructure    *
 *              report. Contains all fields provided by the user on report     *
 *              submission. Validated in ReportService before a Report object  *
 *              is built.                                                      *
 * Author: Adin Hultin                                                         *
 * Edited By:                                                                  *
 * Hector Maes - 04/02/2026                                                    *
 * Date Last Modified: 04/02/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.dto;

public class ReportRequest {

    private String title;
    private String description;
    private String category;
    private String severity;
    private double latitude;
    private double longitude;

    public ReportRequest() {}

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
}
