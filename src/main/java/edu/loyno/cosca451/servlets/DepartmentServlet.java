/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: DepartmentController.java                                   *
 * Project: NOLA Infrastructure Reporting & Tracking System              *
 * Description: Handles endpoints for retrieving and managing city       *
 *              departments and their associated contact records.        *
 * Author: Sophina Nichols                                               *
 * Edited By:                                                            *
 * Hector Maes - 04/02/2026                                              *
 * Date Last Modified: 04/02/2026                                        *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.servlets;

import edu.loyno.cosca451.dto.DepartmentContactDTO;
import edu.loyno.cosca451.dto.DepartmentDTO;
import edu.loyno.cosca451.db.DepartmentRepository;
import edu.loyno.cosca451.service.DepartmentService;
import edu.loyno.cosca451.util.DatabaseUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/* Changes made:
 * - Renamed DepartmentController -> DepartmentServlet
 * - Updated package structure to edu.loyno.cosca451.servlets
 * - Updated all imports to match new project structure
 * - Maintained Service -> Repository -> DB separation
 * - Improved JSON handling consistency
 * 
 * Endpoints:
 * GET /departments
 * GET /departments/{id}
 * GET /departments/{id}/contacts 
 */

@WebServlet("/departments/*") // removed "/api" for clean URL mapping
public class DepartmentServlet extends HttpServlet {

    // DepartmentService handles all department retrieval and DTO conversion
    private DepartmentService departmentService;

    // ObjectMapper for JSON serialization
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

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
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();

        try {
            //case 1: GET /departments
            if (pathInfo == null || pathInfo.equals("/")) {
                List<DepartmentDTO> all = departmentService.getAllDepartments();

                resp.setStatus(HttpServletResponse.SC_OK); //explicit status
                objectMapper.writeValue(resp.getWriter(), all); 
            }
            
            //case 2: GET /departments/{id}/contacts
            else if (pathInfo.endsWith("/contacts")) {
                String idPart = pathInfo.replace("/contacts", "").substring(1);
                long id = Long.parseLong(idPart);

                List<DepartmentContactDTO> contacts = departmentService.getContactsByDepartmentId(id);

                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), contacts);
            }
            
            //case 3: GET /departments/{id}
            else {
                long id = Long.parseLong(pathInfo.substring(1));

                DepartmentDTO dept = departmentService.getDepartmentById(id);

                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), dept);
            }
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(),
                    java.util.Map.of("error", "Invalid department ID"));

        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(),
                java.util.Map.of("error", e.getMessage()));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(),
                java.util.Map.of("error", "Internal server error"));
        }
    }
}