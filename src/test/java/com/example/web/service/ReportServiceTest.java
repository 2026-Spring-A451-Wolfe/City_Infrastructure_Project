/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportServiceTest.java                                              *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: JUnit 5 test suite for ReportService. Covers report submission,  *
 *              status updates, retrieval, filtering, and error handling.        *
 *              Mirror structure of DepartmentServiceTest for consistency.       *
 * Author: Madeline Krehely 
 * - Edited by: Jana El-Khatib 03/20/2026
 *              - Changes: - Added StubReportUpdateRepository — required 
 *                         because ReportService constructor now takes two 
 *                         arguments after ReportUpdateRepository was added 
 *                         as a dependency
 *                         - Updated setUp() to pass both stubs to 
 *                         ReportService constructor                                                *
 * Date Last Modified: 03/20/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.service;

import com.example.web.dto.ReportRequest;
import com.example.web.model.Report;
import com.example.web.repository.ReportRepository;
import com.example.web.model.ReportUpdate;
import com.example.web.repository.ReportUpdateRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//createReport() validates input and saves report
// createReport() throws exceptions for invalid input
// getAllReports() returns correct reports
// getAllReports() returns empty list when no reports exist

class ReportServiceTest {
    private ReportService reportService;

    private StubReportRepository stubRepository;
    private StubReportUpdateRepository stubUpdateRepository;

    @BeforeEach
    void setUp() {
        stubRepository = new StubReportRepository();
        stubUpdateRepository = new StubReportUpdateRepository();
        reportService = new ReportService(stubRepository, stubUpdateRepository);
    }

    /* createReport() Tests */

    @Test
    // Expected Outcome: should create and return report when valid request
    void createReport_validRequest_createsReport() throws SQLException {
        ReportRequest request = new ReportRequest();
        request.setTitle("Pothole");
        request.setDescription("Description");
        request.setCategory("Pothole");
        request.setSeverity("Low");
        request.setLatitude(29.95);
        request.setLongitude(-90.07);
        Report result = reportService.createReport(request, 1L);
        assertNotNull(result);
        assertEquals("Pothole", result.getTitle());
        assertEquals("Requested", result.getStatus());
    }

    @Test
    // Expected Outcome: should throw IllegalArgumentException for null request
    void createReport_nullRequest_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> reportService.createReport(null, 1L));
    }

    @Test
    // Expected Outcome: should throw IllegalArgumentException for blank title
    void createReport_blankTitle_throwsException() {
        ReportRequest request = new ReportRequest();
        request.setTitle("");
        request.setDescription("Description");
        request.setCategory("Pothole");
        request.setSeverity("Low");
        request.setLatitude(29.95);
        request.setLongitude(-90.07);
        assertThrows(IllegalArgumentException.class, () -> reportService.createReport(request, 1L));
    }

    /* getAllReports() Tests */

    @Test
    // Expected Outcome: should return list of reports
    void getAllReports_returnsReports() throws SQLException {
        stubRepository.reports = List
                .of(buildReport(1L, "Pothole", "Description", "Pothole", "Low", 29.95, -90.07, 1L));
        List<Report> result = reportService.getAllReports();
        assertEquals(1, result.size());
        assertEquals("Pothole", result.get(0).getTitle());
    }

    @Test
    // Expected Outcome: should return empty list when no reports
    void getAllReports_emptyList_returnsEmptyList() throws SQLException {
        stubRepository.reports = Collections.emptyList();
        List<Report> result = reportService.getAllReports();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    // Expected Outcome: should return only reports for the requested user
    void getMyReports_returnsUserScopedReports() throws SQLException {
        List<Report> userReports = List.of(
                buildReport(10L, "Flooded Street", "Desc", "Flooding", "High", 29.96, -90.08, 7L),
                buildReport(11L, "Broken Sign", "Desc", "Sign_Damage", "Low", 29.97, -90.09, 7L));
        stubRepository.userReports = userReports;

        List<Report> result = reportService.getMyReports(7L);

        assertEquals(2, result.size());
        assertEquals(7L, result.get(0).getCreatedBy());
        assertEquals(7L, result.get(1).getCreatedBy());
    }

    // tester report
    private Report buildReport(Long id, String title, String description, String category,
            String severity, double latitude, double longitude, long createdBy) {
        Report report = new Report(title, description, category, severity, latitude, longitude, createdBy);
        report.setId(id);
        report.setStatus("Requested");
        report.setCreatedAt(LocalDateTime.now());
        return report;
    }

    // Stub of fake data
    private static class StubReportRepository extends ReportRepository {
        List<Report> reports = Collections.emptyList();
        List<Report> userReports = Collections.emptyList();

        @Override
        public Report save(Report report) {
            report.setId(1L); // Simulate saving
            return report;
        }

        @Override
        public List<Report> findAll() {
            return reports;
        }

        @Override
        public List<Report> findByUserId(long userId) {
            return userReports;
        }
    }

    // Stub of fake data
    private static class StubReportUpdateRepository extends ReportUpdateRepository {
        List<ReportUpdate> updates = Collections.emptyList();

        public StubReportUpdateRepository() {
            super(null); // null DataSource is safe since we override all methods
        }

        @Override
        public List<ReportUpdate> findByReportID(long reportId) {
            return updates;
        }
    }
}