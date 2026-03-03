/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: DepartmentController.java                                   *
 * Project: NOLA Infrastructure Reporting & Tracking System              *
 * Description: Handles endpoints for retrieving and managing city  *
 *              departments and their associated contact records.        *
 * Author: Sophina Nichols                                               *
 * Date Last Modified: 03/03/2026                                        *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.controller;

import com.example.web.dto.DepartmentContactDTO;
import com.example.web.dto.DepartmentDTO;
import com.example.web.repository.DepartmentRepository;
import com.example.web.service.DepartmentService;
import com.example.web.util.DatabaseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/* DepartmentController handles all HTTP requests to /api/departments/*.
 * Endpoints:   GET /api/departments
 *              GET /api/departments/{id}
 *              GET /api/departments/{id}/contacts
 * This controller delegates all business logic to DepartmentService.
 * Departments are managed directly in the database by admin, and because 
 * they are publicly accessible, no authentication measures are required.
 */

@WebServlet("/api/departments/*")
public class DepartmentController extends HttpServlet {
    // DepartmentService handles all department retrieval and DTO conversion
    private DepartmentService departmentService;
    // ObjectMapper converts Java objects to JSON for the response
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        departmentService = new DepartmentService(
            new DepartmentRepository(DatabaseUtil.getDataSource())
        );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<DepartmentDTO> all = departmentService.getAllDepartments();
                resp.getWriter().write(objectMapper.writeValueAsString(all));
            } else if (pathInfo.endsWith("/contacts")) {
                String idPart = pathInfo.replace("/contacts", "").substring(1);
                long id = Long.parseLong(idPart);
                List<DepartmentContactDTO> contacts = departmentService.getContactsByDepartmentId(id);
                resp.getWriter().write(objectMapper.writeValueAsString(contacts));
            } else {
                long id = Long.parseLong(pathInfo.substring(1));
                DepartmentDTO dept = departmentService.getDepartmentById(id);
                resp.getWriter().write(objectMapper.writeValueAsString(dept));
            }
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid department ID\"}");
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
}