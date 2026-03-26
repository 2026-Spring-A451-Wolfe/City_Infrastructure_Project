/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ImageController.java                                              *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Handles HTTP requests for image upload endpoints. Delegates to * 
 *              ImageStorageService for file validation and storage. Exposes   *
 *              POST /api/reports/{id}/images. Auth required (Citizen/Admin).  *
 *              Reference AuthController for structure and JWT validation.     *
 * Author: Jana El-Khatib
 *         - Changes: - Changed @WebServlet from "/api/reports/*" to 
 *                    "/api/images/*" to resolve servlet conflict with 
 *                     ReportController (both were mapped to "/api/reports/*" 
 *                     which caused Tomcat to refuse to deploy the app)      
 *                     - Updated path matching from "/\\d+/images" to "/\\d+" 
 *                       to match new URL
 *                     - Added logging statements                           
 * Date Last Modified: 03/25/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.controller;

import com.example.web.model.ReportImage;
import com.example.web.repository.ReportImageRepository;
import com.example.web.service.ImageStorageService;
import com.example.web.util.DatabaseUtil;
import com.example.web.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/* ImageController handles HTTP requests to upload report images.
 * Endpoint:    POST /api/images/*
 * Auth:        Required (Citizen/Admin)
 * This controller is responsible only for reading requests,
 * validating JWT access, passing image data to ImageStorageService,
 * and writing JSON responses back to the client.
 */

@WebServlet("/api/images/*")
@MultipartConfig
public class ImageController extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ImageController.class.getName());

    private ImageStorageService imageStorageService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public void init() {
        ReportImageRepository reportImageRepository = new ReportImageRepository(DatabaseUtil.getDataSource());
        // Webapp is being developed in Tomcat container so the store uploads will be
        // in a folder here when running
        String uploadDirectory = getServletContext().getRealPath("/uploads");

        imageStorageService = new ImageStorageService(reportImageRepository, uploadDirectory);

        logger.info("ImageController initialized successfully");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String path = req.getPathInfo();
        logger.info("POST /api/images" + path + " request received");

        resp.setContentType("application/json");

        if (path != null && path.matches("/\\d+")) {
            handleImageUpload(req, resp);
        } else {
            logger.warning("POST /api/images FAILED — path not found: " + path);
            resp.setStatus(404);
            resp.getWriter().write("{\"error\": \"Not found\"}");
        }
    }
    // -------------------------------------------------------------------------
    // GET /api/images/report/{reportId} — get all images for a report
    // -------------------------------------------------------------------------

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String path = req.getPathInfo();
        logger.info("GET /api/images" + path + " request received");

        resp.setContentType("application/json");

        if (path != null && path.matches("/report/\\d+")) {
            try {
                long reportId = Long.parseLong(path.split("/")[2]);

                ReportImageRepository repo = new ReportImageRepository(DatabaseUtil.getDataSource());

                List<ReportImage> images = repo.findByReportId(reportId);

                resp.setStatus(200);
                resp.getWriter().write(objectMapper.writeValueAsString(images));

                logger.info("SUCCESS: Returned " + images.size()
                        + " images for report ID " + reportId);

            } catch (Exception e) {
                logger.severe("FAILED: Error retrieving images — " + e.getMessage());
                resp.setStatus(500);
                resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            }
        } else {
            logger.warning("GET /api/images FAILED — path not found: " + path);
            resp.setStatus(404);
            resp.getWriter().write("{\"error\": \"Not found\"}");
        }
    }

    private void handleImageUpload(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        try {
            // Read the Authorization header
            String authHeader = req.getHeader("Authorization"); // Token must exist and follow the "Bearer <token>"
                                                                // format

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warning("FAILED: Missing or malformed token");
                resp.setStatus(401);
                resp.getWriter().write("{\"error\": \"Missing or invalid token\"}");
                return;
            }

            // Remove prefix to get the raw JWT string
            String token = authHeader.substring(7);
            // Extract role from token claims
            String role = JwtUtil.getRole(token);

            // Only Citizen or Admin can upload images
            if (!"Citizen".equals(role) && !"Admin".equals(role)) {
                logger.warning("FAILED: Forbidden access — role: " + role);
                resp.setStatus(403);
                resp.getWriter().write("{\"error\": \"Forbidden\"}");
                return;
            }

            // Extract report ID from path: /{id}/images
            String[] pathParts = req.getPathInfo().split("/");
            Integer reportId = Integer.parseInt(pathParts[1]);

            // Read uploaded file part
            Part filePart = req.getPart("image");

            if (filePart == null || filePart.getSize() == 0) {
                logger.warning("FAILED: No image file provided");
                resp.setStatus(400);
                resp.getWriter().write("{\"error\": \"Image file is required\"}");
                return;
            }

            String originalFilename = filePart.getSubmittedFileName();

            // Save uploaded content temporarily before service processes it
            File tempFile = File.createTempFile("upload_", ".tmp");
            filePart.write(tempFile.getAbsolutePath());

            // Delegate to ImageStorageService for validation and storage
            ReportImage image = imageStorageService.saveImage(
                    tempFile,
                    originalFilename,
                    filePart.getContentType(),
                    filePart.getSize(),
                    reportId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imageId", image.getId());

            resp.setStatus(201);
            resp.getWriter().write(objectMapper.writeValueAsString(response));

            logger.info("SUCCESS: Image uploaded — reportId: "
                    + reportId + ", imageId: " + image.getId());

        } catch (NumberFormatException e) {
            logger.warning("FAILED: Invalid report ID — " + e.getMessage());
            resp.setStatus(400);
        } catch (Exception e) {
            logger.severe("FAILED: Image upload error — " + e.getMessage());
            resp.setStatus(400);
        }
    }
}