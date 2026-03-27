/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportImage.java                                                  *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Handles all HTTP requests and responses for report-related     *
 *              endpoints. Parses requests, delegates to ReportService, and    *
 *              writes JSON responses. No business logic belongs here.         *
 * Author: Adin Hultin                                                         *         
 * -Edited by Ethan DeLaRosa on 3/15                                           *    
 * - Edited by Madeline Krehely 3/19   
 * - Edited By: Jana El-Khatib 03/25/2026
 *          - Changes: - Changed @WebServlet from "/api/reports/*" to support
 *                      routing to /api/reports/{id} and 
 *                      /api/reports{id}/updates
 *                     - Added doGet routing for GET /api/reports/{id} and 
 *                      /api/reports/{id}/updates 
 *                     - Fixed hardcoded createdBy = 2L — now reads userId 
 *                      from JWT filter attr  
 *                     - Added doPut for PUT /api/reports/{id}/status 
 *                          (Admin only)               
 *                     - Added doDelete for DELETE /api/reports/{id} 
 *                          (Admin only) 
 *                      - Added a GET /api/reports/me will return the reports
 *                          of the user
 *                      - Added logging statements         
 * Date Last Modified: 03/25/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.example.web.controller;

import com.example.web.dto.ReportRequest;
import com.example.web.dto.ReportUpdateDTO;
import com.example.web.model.Report;
import com.example.web.model.ReportUpdate;
import com.example.web.repository.ReportRepository;
import com.example.web.repository.ReportUpdateRepository;
import com.example.web.service.ReportService;
import com.example.web.util.DatabaseUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/api/reports/*")
public class ReportController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ReportController.class.getName());

    private final ReportService reportService = new ReportService(
            new ReportRepository(),
            new ReportUpdateRepository(DatabaseUtil.getDataSource()));
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    // -------------------------------------------------------------------------
    // GET /api/reports
    // GET /api/reports/{id}
    // GET /api/reports/{id}/updates
    // -------------------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("GET /api/reports hit — path: " + request.getPathInfo());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/reports
                List<Report> reports = reportService.getAllReports();
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getWriter(), reports);
                logger.info("SUCCESS — returned " + reports.size() + " reports");

            } else if (pathInfo.matches("/\\d+")) {
                // GET /api/reports/{id}
                long id = Long.parseLong(pathInfo.substring(1));
                Report report = reportService.getReportById(id);
                if (report == null) {
                    logger.warning("FAILED — report not found: " + id);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    objectMapper.writeValue(response.getWriter(),
                            Map.of("error", "Report not found"));
                } else {
                    response.setStatus(HttpServletResponse.SC_OK);
                    objectMapper.writeValue(response.getWriter(), report);
                    logger.info("SUCCESS — returned report ID: " + id);
                }

            } else if (pathInfo.matches("/\\d+/updates")) {
                // GET /api/reports/{id}/updates
                long id = Long.parseLong(pathInfo.split("/")[1]);
                List<?> updates = reportService.getUpdatesByReportId(id);
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getWriter(), updates);
                logger.info("SUCCESS — returned " + updates.size() + " updates for report ID: " + id);

            } else if (pathInfo.equals("/my")) {
                // GET /api/reports/my - returns current user's reports
                long userId = (long) request.getAttribute("userId");
                List<Report> reports = reportService.getMyReports((userId));
                response.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(response.getWriter(), reports);
                logger.info("SUCCESS — userId: " + userId + " returned " + reports.size() + " reports");

            } else {
                logger.warning("FAILED — path not found: " + pathInfo);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(response.getWriter(),
                        Map.of("error", "Not found"));
            }

        } catch (NumberFormatException e) {
            logger.warning("FAILED — invalid report ID: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Invalid report ID"));
        } catch (SQLException e) {
            logger.severe("FAILED — database error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Database error", "details", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // POST /api/reports — create a new report
    // -------------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("POST /api/reports hit — userId: " + request.getAttribute("userId"));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            ReportRequest reportRequest = objectMapper.readValue(request.getInputStream(), ReportRequest.class);

            long createdBy = (long) request.getAttribute("userId");

            Report savedReport = reportService.createReport(reportRequest, createdBy);

            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(response.getWriter(), savedReport);

            logger.info("SUCCESS — reportId: " + savedReport.getId() + " createdBy: " + createdBy);

        } catch (IllegalArgumentException e) {
            logger.warning("FAILED — validation error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            logger.severe("FAILED — database error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Failed to save report.", "details", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // PUT /api/reports/{id}/status — Admin only, update report status
    // -------------------------------------------------------------------------
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("PUT /api/reports hit — path: " + request.getPathInfo());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || !pathInfo.matches("/\\d+/status")) {
            logger.warning("FAILED — path not found: " + pathInfo);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(response.getWriter(), Map.of("error", "Not found"));
            return;
        }

        try {
            // Enforce Admin only
            String role = (String) request.getAttribute("role");
            if (!"Admin".equals(role)) {
                logger.warning("FAILED — forbidden, role: " + role);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                objectMapper.writeValue(response.getWriter(),
                        Map.of("error", "Forbidden", "message", "Admin access required."));
                return;
            }

            long reportId = Long.parseLong(pathInfo.split("/")[1]);
            long updaterId = (long) request.getAttribute("userId");

            ReportUpdateDTO statusRequest = objectMapper.readValue(
                    request.getInputStream(), ReportUpdateDTO.class);

            ReportUpdate update = reportService.updateStatus(reportId, updaterId, statusRequest);

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), update);

            logger.info("SUCCESS — reportId: " + reportId + " newStatus: "
                    + statusRequest.getNewStatus() + " updatedBy: " + updaterId);

        } catch (IllegalArgumentException e) {
            logger.warning("FAILED — validation error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            logger.severe("FAILED — database error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Database error", "details", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // DELETE /api/reports/{id} - Admin only, delete a report
    // -------------------------------------------------------------------------
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("DELETE /api/reports hit — path: " + request.getPathInfo());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            logger.warning("FAILED — path not found: " + pathInfo);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(response.getWriter(), Map.of("error", "Not found"));
            return;
        }

        try {
            String role = (String) request.getAttribute("role");
            if (!"Admin".equals(role)) {
                logger.warning("FAILED — forbidden, role: " + role);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                objectMapper.writeValue(response.getWriter(),
                        Map.of("error", "Forbidden", "message", "Admin access required."));
                return;
            }

            long reportId = Long.parseLong(pathInfo.substring(1));
            reportService.deleteReport(reportId);

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("success", true, "message", "Report deleted successfully."));

            logger.info("SUCCESS — deleted reportId: " + reportId
                    + " by userId: " + request.getAttribute("userId"));

        } catch (IllegalArgumentException e) {
            logger.warning("FAILED — " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(), Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            logger.severe("FAILED — database error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Database error", "details", e.getMessage()));
        }
    }
}