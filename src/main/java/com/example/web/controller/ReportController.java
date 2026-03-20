/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportImage.java                                                  *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Handles all HTTP requests and responses for report-related     *
 *              endpoints. Parses requests, delegates to ReportService, and    *
 *              writes JSON responses. No business logic belongs here.         *
 * Author: Adin Hultin                                                         *         
 * -Edited by Ethan DeLaRosa on 3/15                                           *    
 * - Edited by Madeline Krehely 3/19                                           *           
 * Date Last Modified: 03/19/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.example.web.controller;

import com.example.web.dto.ReportRequest;
import com.example.web.model.Report;
import com.example.web.service.ReportService;
import com.example.web.repository.ReportRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reports")
public class ReportController extends HttpServlet {

    private final ReportService reportService = new ReportService(new ReportRepository());
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Report> reports = reportService.getAllReports();
            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), reports);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Failed to fetch reports.", "details", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            ReportRequest reportRequest = objectMapper.readValue(request.getInputStream(), ReportRequest.class);

            // TEMPORARY until login/session is wired up.
            // Use an existing seeded citizen account ID.
            long createdBy = (long) request.getAttribute("userID");

            Report savedReport = reportService.createReport(reportRequest, createdBy);

            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(response.getWriter(), savedReport);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", e.getMessage()));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Failed to save report.", "details", e.getMessage()));
        }
    }
}