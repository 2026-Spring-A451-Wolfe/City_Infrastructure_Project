/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ImageController.java                                              *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Handles HTTP requests for image upload endpoints. Delegates to * 
 *              ImageStorageService for file validation and storage. Exposes   *
 *              POST /api/reports/{id}/images. Auth required (Citizen/Admin).  *
 *              Reference AuthController for structure and JWT validation.     *
 * Author: Jana El-Khatib                                                   *
 * Date Last Modified: 03/13/2026                                              *
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
import java.util.Map;

/* ImageController handles HTTP requests to upload report images.
 * Endpoint:    POST /api/reports/{id}/images
 * Auth:        Required (Citizen/Admin)
 * This controller is responsible only for reading requests,
 * validating JWT access, passing image data to ImageStorageService,
 * and writing JSON responses back to the client.
 */

@WebServlet("/api/images/*")
@MultipartConfig
public class ImageController extends HttpServlet {

    private ImageStorageService imageStorageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        ReportImageRepository reportImageRepository = new ReportImageRepository(DatabaseUtil.getDataSource());
        String uploadDirectory = getServletContext().getRealPath("/uploads"); // Webapp is being developed in Tomcat container so the store uploads will be in a folder here when running
        imageStorageService = new ImageStorageService(reportImageRepository, uploadDirectory);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setContentType("application/json");
        String path = req.getPathInfo();

        if (path != null && path.matches("/\\d+")) {
            handleImageUpload(req, resp);
        } else {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\": \"Not found\"}");
        }
    }

    private void handleImageUpload(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            // Read the Authorization header
            String authHeader = req.getHeader("Authorization");

            // Token must exist and follow the "Bearer <token>" format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
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
                resp.setStatus(403);
                resp.getWriter().write("{\"error\": \"Forbidden\"}");
                return;
            }

            // Extract report ID from path: /{id}/images
            String path = req.getPathInfo();
            String[] pathParts = path.split("/");
            
            Integer reportId = Integer.parseInt(pathParts[1]);

            // Read uploaded file part
            Part filePart = req.getPart("image");

            if (filePart == null || filePart.getSize() == 0) {
                resp.setStatus(400);
                resp.getWriter().write("{\"error\": \"Image file is required\"}");
                return;
            }

            String originalFilename = filePart.getSubmittedFileName();
            String contentType = filePart.getContentType();
            long fileSize = filePart.getSize();

            // Save uploaded content temporarily before service processes it
            File tempFile = File.createTempFile("upload_", ".tmp");
            filePart.write(tempFile.getAbsolutePath());

            // Delegate to ImageStorageService for validation and storage
            ReportImage image = imageStorageService.saveImage(
                tempFile,
                originalFilename,
                contentType,
                fileSize,
                reportId
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image uploaded successfully");
            response.put("imageId", image.getId());
            response.put("storedFilename", image.getStoredFilename());

            resp.setStatus(201);
            resp.getWriter().write(objectMapper.writeValueAsString(response));

        } catch (NumberFormatException e) {
            resp.setStatus(400);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid report ID");
            resp.getWriter().write(objectMapper.writeValueAsString(error));
        } catch (Exception e) {
            resp.setStatus(400);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            resp.getWriter().write(objectMapper.writeValueAsString(error));
        }
    }
}
