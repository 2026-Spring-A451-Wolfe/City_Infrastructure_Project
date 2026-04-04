/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportService.java                                                *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Contains all business logic for report management. Validates   *
 *              inputs, enforces access rules, coordinates between             *
 *              ReportRepository and other repositories, and maps results      *
 *              to DTOs before returning them to ReportController.             *
 * Author: Adin Hultin                                                         *
 * Date Last Modified: 03/05/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package edu.loyno.cosca451.service;

import edu.loyno.cosca451.dto.ReportRequest;
import edu.loyno.cosca451.model.Report;
import edu.loyno.cosca451.db.ReportDAO;

import java.sql.SQLException;
import java.util.List;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    public Report createReport(ReportRequest request, long createdBy) throws SQLException {
        validate(request);

        Report report = new Report(
                request.getTitle().trim(),
                request.getDescription().trim(),
                request.getCategory().trim(),
                request.getSeverity().trim(),
                request.getLatitude(),
                request.getLongitude(),
                createdBy
        );

        report.setStatus("Requested");
        return reportDAO.save(report);
    }

    public List<Report> getAllReports() throws SQLException {
        return reportDAO.findAll();
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
