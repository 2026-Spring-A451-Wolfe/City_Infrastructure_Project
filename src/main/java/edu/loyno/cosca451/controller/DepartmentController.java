/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: DepartmentController.java                                   *
 * Project: NOLA Infrastructure Reporting & Tracking System              *
 * Description: Handles endpoints for retrieving and managing city       *
 *              departments and their associated contact records.        *
 * Author: Sophina Nichols 
 * - Edited By: Jana El-Khatib 03/25/2026
 *          - Changes: Added structured logging using java.util.Logger   
 * Date Last Modified: 03/25/2026                                        *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.controller;

import edu.loyno.cosca451.dto.DepartmentContactDTO;
import edu.loyno.cosca451.dto.DepartmentDTO;
import edu.loyno.cosca451.repository.DepartmentRepository;
import edu.loyno.cosca451.service.DepartmentService;
import edu.loyno.cosca451.util.DatabaseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/api/departments/*")
public class DepartmentController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DepartmentController.class.getName());

    private DepartmentService departmentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        departmentService = new DepartmentService(
                new DepartmentRepository(DatabaseUtil.getDataSource()));
        logger.info("DepartmentController initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String pathInfo = req.getPathInfo();
        logger.info("GET /api/departments" + (pathInfo != null ? pathInfo : "") + " request received");

        resp.setContentType("application/json");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {

                List<DepartmentDTO> all = departmentService.getAllDepartments();
                resp.getWriter().write(objectMapper.writeValueAsString(all));

                logger.info("SUCCESS: Returned " + all.size() + " departments");

            } else if (pathInfo.endsWith("/contacts")) {

                String idPart = pathInfo.replace("/contacts", "").substring(1);
                long id = Long.parseLong(idPart);

                List<DepartmentContactDTO> contacts =
                        departmentService.getContactsByDepartmentId(id);

                resp.getWriter().write(objectMapper.writeValueAsString(contacts));

                logger.info("SUCCESS: Returned " + contacts.size()
                        + " contacts for department ID " + id);

            } else {

                long id = Long.parseLong(pathInfo.substring(1));
                DepartmentDTO dept = departmentService.getDepartmentById(id);

                resp.getWriter().write(objectMapper.writeValueAsString(dept));

                logger.info("SUCCESS: Returned department ID " + id
                        + " (Name: " + dept.getName() + ")");
            }

        } catch (NumberFormatException e) {

            logger.warning("FAILED: Invalid department ID in request — " + pathInfo);

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid department ID\"}");

        } catch (RuntimeException e) {

            logger.warning("FAILED: Department not found — " + e.getMessage());

            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");

        } catch (Exception e) {

            logger.severe("FAILED: Internal server error — " + e.getMessage());

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
}