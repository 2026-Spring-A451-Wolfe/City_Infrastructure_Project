/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportUpdate.java                                                  *
 * Project: NOLA Infrastructure Reporting & Tracking System                     *
 * Description: Represents a status update made to a report, including the     *
 *              previous and new status, department, and user who updated it.  *
 * Author: Anderson Varela Suarez                                                     *
 * Date Last Modified: 03/06/2026                                               *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.model;

import java.time.LocalDateTime;

public class ReportUpdate {

    private long id;
    private long reportId;
    private long updaterId;
    private String oldStatus;
    private String newStatus;
    private long departmentId;
    private String comment;
    private LocalDateTime updatedAt;

    public ReportUpdate() {}

    public ReportUpdate(long reportId, long updaterId, String oldStatus, String newStatus,
                        long departmentId, String comment) {
        this.reportId = reportId;
        this.updaterId = updaterId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.departmentId = departmentId;
        this.comment = comment;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getReportId() { return reportId; }
    public void setReportId(long reportId) { this.reportId = reportId; }

    public long getUpdaterId() { return updaterId; }
    public void setUpdaterId(long updaterId) { this.updaterId = updaterId; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public long getDepartmentId() { return departmentId; }
    public void setDepartmentId(long departmentId) { this.departmentId = departmentId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}