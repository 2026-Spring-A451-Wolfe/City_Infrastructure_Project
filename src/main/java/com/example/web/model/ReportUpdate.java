/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportUpdate.java                                                   *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: Represents a status update made to a report, including the       *
 *              previous and new status, department, and user who updated it.    *
 * Author: Anderson Varela Suarez, capitalization tweaked by Madeline 3/16       *
 * Date Last Modified: 03/06/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.model;

import java.time.LocalDateTime;

public class ReportUpdate {

    private long id;
    private long reportID;
    private long updaterID;
    private String oldStatus;
    private String newStatus;
    private long departmentID;
    private String comment;
    private LocalDateTime updatedAt;

    public ReportUpdate() {}

    public ReportUpdate(long reportID, long updaterID, String oldStatus, String newStatus,
                        long departmentID, String comment) {
        this.reportID = reportID;
        this.updaterID = updaterID;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.departmentID = departmentID;
        this.comment = comment;
    }

    public long getID() { return id; }
    public void setID(long id) { this.id = id; }

    public long getReportID() { return reportID; }
    public void setReportID(long reportID) { this.reportID = reportID; }

    public long getUpdaterID() { return updaterID; }
    public void setUpdaterID(long updaterID) { this.updaterID = updaterID; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public long getDepartmentID() { return departmentID; }
    public void setDepartmentID(long departmentID) { this.departmentID = departmentID; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}