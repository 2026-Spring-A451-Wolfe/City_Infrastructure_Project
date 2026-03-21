/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportService.java                                                *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Contains all business logic for report management. Validates   *
 *              inputs, enforces access rules, coordinates between             *
 *              ReportRepository and other repositories, and maps results      *
 *              to DTOs before returning them to ReportController.             *
 * Author: Adin Hultin                                                         *
 * Edited: Madeline Krehely 3/19
 * - Edited By: Jana El-Khatib 03/20/2026
 *          - Changes: - Added ReportUpdateRepository as second 
 *                      constructor argument            
 *                     - Added getReportById(long id)                                            
 *                     - Added getUpdatesByReportId(long reportId)                               
 *                     - Added updateStatus(long reportId, long updaterId, 
 *                          ReportUpdateDTO)  
 *                     - Added deleteReport(long reportId)
 *                     - Added getMyReports(long userId)                                       
 * Date Last Modified: 03/20/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.example.web.service;

import com.example.web.dto.ReportRequest;
import com.example.web.dto.ReportUpdateDTO;
import com.example.web.model.Report;
import com.example.web.repository.ReportUpdateRepository;
import com.example.web.model.ReportUpdate;
import com.example.web.repository.ReportRepository;

import java.sql.SQLException;
import java.util.List;

public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportUpdateRepository reportUpdateRepository;

    public ReportService(ReportRepository reportRepository, ReportUpdateRepository reportUpdateRepository) {
        this.reportRepository = reportRepository;
        this.reportUpdateRepository = reportUpdateRepository;
    }

    public Report createReport(ReportRequest request, long createdBy) throws SQLException {
        validate(request);

        Report report = new Report(
                request.getTitle().trim(),
                request.getDescription().trim(),
                request.getCategory().trim(),
                request.getSeverity().trim(),
                request.getLatitude(),
                request.getLongitude(),
                createdBy);

        report.setStatus("Requested");
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() throws SQLException {
        return reportRepository.findAll();
    }

    public Report getReportById(long id) throws SQLException {
        return reportRepository.findById(id);
    }

    public List<ReportUpdate> getUpdatesByReportId(long reportId) throws SQLException {
        return reportUpdateRepository.findByReportID(reportId);
    }

    public ReportUpdate updateStatus(long reportId, long updaterId, ReportUpdateDTO request)
            throws SQLException {
        // Validate the new status value against allowed values from spec
        String newStatus = request.getNewStatus();
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("New status is required.");
        }

        if (!newStatus.equals("Requested") &&
                !newStatus.equals("Open") &&
                !newStatus.equals("In_Progress") &&
                !newStatus.equals("Resolved") &&
                !newStatus.equals("Closed") &&
                !newStatus.equals("Rejected")) {
            throw new IllegalArgumentException("Invalid status value.");
        }

        // Fetch the current report to get the old status
        Report report = reportRepository.findById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("Report not found.");
        }

        String oldStatus = report.getStatus();

        // Build the ReportUpdate record
        ReportUpdate update = new ReportUpdate();
        update.setReportID(reportId);
        update.setUpdaterID(updaterId);
        update.setOldStatus(oldStatus);
        update.setNewStatus(newStatus);
        update.setComment(request.getComment());

        // Save the update record
        ReportUpdate savedUpdate = reportUpdateRepository.save(update);

        // Update the report's status in the reports table
        reportRepository.updateStatus(reportId, newStatus);

        return savedUpdate;
    }

    public void deleteReport(long reportId) throws SQLException {
        Report report = reportRepository.findById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("Report not found.");
        }
        reportRepository.delete(reportId);
    }

    public List<Report> getMyReports(long userId) throws SQLException {
        return reportRepository.findByUserId(userId);
    }

    private void validate(ReportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is missing.");
        }
        if (isBlank(request.getTitle())) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (isBlank(request.getDescription())) {
            throw new IllegalArgumentException("Description is required.");
        }
        if (isBlank(request.getCategory())) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (isBlank(request.getSeverity())) {
            throw new IllegalArgumentException("Severity is required.");
        }

        String category = request.getCategory().trim();
        if (!category.equals("Pothole") &&
                !category.equals("Flooding") &&
                !category.equals("Streetlight") &&
                !category.equals("Sign_Damage") &&
                !category.equals("Road_Damage") &&
                !category.equals("Debris") &&
                !category.equals("Other")) {
            throw new IllegalArgumentException("Invalid category.");
        }

        String severity = request.getSeverity().trim();
        if (!severity.equals("Low") &&
                !severity.equals("Medium") &&
                !severity.equals("High") &&
                !severity.equals("Critical")) {
            throw new IllegalArgumentException("Invalid severity.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
