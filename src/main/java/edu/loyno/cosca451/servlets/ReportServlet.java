/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportImage.java                                                  *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Handles all HTTP requests and responses for report-related     *
 *              endpoints. Parses requests, delegates to ReportService, and    *
 *              writes JSON responses. No business logic belongs here.         *
 * Author: Adin Hultin                                                         *
 * -Edited by:                                                                 *
 * -Ethan DeLaRosa on 3/15                                                     *
 * -Hector Maes on 03/27/26                                                        *
 * Date Last Modified: 03/27/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package edu.loyno.cosca451.servlets;

import edu.loyno.cosca451.model.Report;
import edu.loyno.cosca451.dto.ReportRequest;
import edu.loyno.cosca451.service.ReportService;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
//import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/reports") //changed ("/api/reports" since Wolfe said he wanted "clean URL mappings")
public class ReportServlet extends HttpServlet { //ReportService changed to ReportServlet

    private final ReportService reportService = new ReportService();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    //get fetches all reports
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Report> reports = reportService.getAllReports();

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), reports);

} catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Failed to fetch reports.", "details", e.getMessage()));
        }
    }

    //post creates a report
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            //parses incoming JSON request boy into DTO
            ReportRequest reportRequest = objectMapper.readValue(request.getInputStream(), ReportRequest.class);

            // TEMPORARY until login/session is wired up.
            // Use an existing seeded citizen account ID.
            long createdBy = 2L;

            //call service layer
            Report savedReport = reportService.createReport(reportRequest, createdBy);

            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(response.getWriter(), savedReport);

        } catch (IllegalArgumentException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", e.getMessage()));

        } catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(),
                    Map.of("error", "Failed to save report.", "details", e.getMessage()));
        }
    }
}