/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Filename: AuthController.java                                             *
 * Project: NOLA Infrastructure Reporting & Tracking System                  *
 * Description: Handles authentication endpoints for user registration and   *
 *              login, returning JWT tokens upon successful authentication.  *
 * Author: Sophina Nichols                                                   *
 * Edited by:                                                                *
 * Hector Maes - 04/02/26                                                    *
 * Date Last Modified: 04/02/2026                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.servlets;

import edu.loyno.cosca451.dto.LoginRequest;
import edu.loyno.cosca451.dto.RegisterRequest;
import edu.loyno.cosca451.model.User;
import edu.loyno.cosca451.service.AuthService;
import edu.loyno.cosca451.db.UserDAO;
import edu.loyno.cosca451.util.DatabaseUtil;
import edu.loyno.cosca451.util.JwtUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* Changes made:
 * - Renamed AuthController -> AuthServlet
 * - Updated package to edu.loyno.cosca451.servlets
 * - Updated all imports to new package structure
 * - Replaced UserRepository with user DAO (JDBC separation)
 * 
 * Endpoints:
 * POST /auth/register
 * POST /auth/login
 * GET  /auth/me
 */

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    private AuthService authService;
    // ObjectMapper converts Java objects to/from JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() {
        //DAO handles DB, not servlet
        UserDAO userDAO = new UserDAO(DatabaseUtil.getDataSource());
        authService = new AuthService(userDAO);
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
            //Fix: use getReader()
            RegisterRequest request = objectMapper.readValue(req.getReader(), RegisterRequest.class);

            // Delegate to AuthService which handles validation, hashing, and saving
            User user = authService.register(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("username", user.getUsername());

            resp.setStatus(201);    // Success (201)
            resp.getWriter().write(objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            resp.setStatus(400);    // Error (400): Validation failed or username/email/phone is taken
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            resp.getWriter().write(objectMapper.writeValueAsString(error));
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            //Fix: use getReader()
            LoginRequest request = objectMapper.readValue(req.getReader(), LoginRequest.class);
            
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