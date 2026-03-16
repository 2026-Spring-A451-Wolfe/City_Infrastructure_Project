/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportUpdateDTO.java                                      *
 * Project: NOLA Infrastructure Reporting & Tracking System            *
 * Description: Data Transfer Object used to transfer report update    *
 *              information between the backend and client.            *
 * Author: Anderson Varela Suarez                                      *
 * Date Last Modified: 03/09/2026                                      *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.dto;

/* ReportUpdateDTO is a DTO that represents a status update made to a
 * report in an API response.
 */

public class ReportUpdateDTO {

    private long id;
    private long reportId;
    private long updaterId;
    private String oldStatus;
    private String newStatus;
    private long departmentId;
    private String comment;
    private String updatedAt;

    public ReportUpdateDTO() {}

    public ReportUpdateDTO(long id, long reportId, long updaterId,
                           String oldStatus, String newStatus,
                           long departmentId, String comment,
                           String updatedAt) {
        this.id = id;
        this.reportId = reportId;
        this.updaterId = updaterId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.departmentId = departmentId;
        this.comment = comment;
        this.updatedAt = updatedAt;
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

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}