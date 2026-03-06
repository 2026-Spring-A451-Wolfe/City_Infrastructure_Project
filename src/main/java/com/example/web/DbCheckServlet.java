 /**************************************************************************
 * Filename: DbCheckServlet.java
 * Project: Infrastructure Reporting & Tracking System
 * Description: Provides an endpoint used to verify database connectivity and
 *              confirm successful application-to-database communication.
 * Author: Sophina Nichols
 * Date Last Modified: 03/03/2026
 **************************************************************************/

package com.example.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

@WebServlet("/db-check")
public class DbCheckServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String jdbcUrl = valueOrDefault("JDBC_URL", "jdbc:postgresql://db:5432/demo");
        String user = valueOrDefault("DB_USER", "demo");
        String password = valueOrDefault("DB_PASSWORD", "demo");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        try (Connection ignored = DriverManager.getConnection(jdbcUrl, user, password)) {
            response.getWriter().write("DB OK");
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("DB FAIL: " + exception.getMessage());
        }
    }

    private String valueOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
}
