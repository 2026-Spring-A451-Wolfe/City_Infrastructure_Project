/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Filename: AuthController.java                                             *
 * Project: NOLA Infrastructure Reporting & Tracking System                  *
 * Description: Handles authentication endpoints for user registration and   *
 *              login, returning JWT tokens upon successful authentication.  *
 * Author: Sophina Nichols                                                   *
 * Date Last Modified: 03/03/2026                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.controller;

import com.example.web.dto.LoginRequest;
import com.example.web.dto.RegisterRequest;
import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import com.example.web.service.AuthService;
import com.example.web.util.DatabaseUtil;
import com.example.web.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* AuthController handles all HTTP requests to /api/auth/*.
 * Endpoints:   POST /api/auth/register
 *              POST /api/auth/login
 *              GET  /api/auth/me 
 * This controller delegates all login and registration logic to AuthService.
 * It is responsible only for reading requests, calling the service,
 * and writing JSON responses back to the client.
 */

@WebServlet("/api/auth/*")
public class AuthController extends HttpServlet {

    private AuthService authService;
    // ObjectMapper converts Java objects to/from JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        UserRepository userRepository = new UserRepository(DatabaseUtil.getDataSource());
        authService = new AuthService(userRepository);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String path = req.getPathInfo();

        if ("/register".equals(path)) {
            handleRegister(req, resp);
        } else if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else {
            resp.setStatus(404);    // Error (404): No matching route found
            resp.getWriter().write("{\"error\": \"Not found\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String path = req.getPathInfo();

        if ("/me".equals(path)) {
            handleMe(req, resp);
        } else {
            resp.setStatus(404);    // Error (404): No matching route found
            resp.getWriter().write("{\"error\": \"Not found\"}");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Deserialize JSON request body into a RegisterRequest object
            RegisterRequest request = objectMapper.readValue(req.getInputStream(), RegisterRequest.class);

            // Delegate to AuthService which handles validation, hashing, and saving
            User user = authService.register(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("username", user.getUsername());

            resp.setStatus(201);    // Success (201)
            resp.getWriter().write(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            resp.setStatus(400);    // Error (400): Validation failed or username/email is taken
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            resp.getWriter().write(objectMapper.writeValueAsString(error));
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Deserialize JSON request body into a LoginRequest object
            LoginRequest request = objectMapper.readValue(req.getInputStream(), LoginRequest.class);
            
            // Delegate to AuthService which verifies credentials and generates JWT
            String token = authService.login(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);

            resp.setStatus(200);    // Success (200)
            resp.getWriter().write(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            resp.setStatus(401);    // Error (401): Credentials are invalid
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            resp.getWriter().write(objectMapper.writeValueAsString(error));
        }
    }

    private void handleMe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Read the Authorization header
            String authHeader = req.getHeader("Authorization");

            // Token must exist and follow the "Bearer <token>" format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                resp.setStatus(401);    // Error (401): Token is missing, invalid, or expired
                resp.getWriter().write("{\"error\": \"Missing or invalid token\"}");
                return;
            }
            // Remove prefix to get the raw JWT string
            String token = authHeader.substring(7);

            // Extract user information from the token claims
            long userId = JwtUtil.getUserId(token);
            String role = JwtUtil.getRole(token);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("role", role);

            resp.setStatus(200);    // Success (200)
            resp.getWriter().write(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            resp.setStatus(401);    // Error (401): Token is missing, invalid, or expired
            resp.getWriter().write("{\"error\": \"Invalid or expired token\"}");
        }
    }
}