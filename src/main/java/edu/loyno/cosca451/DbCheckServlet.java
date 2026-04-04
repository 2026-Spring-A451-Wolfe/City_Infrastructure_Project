<<<<<<< HEAD
/**************************************************************************
 * Filename: DbCheckServlet.java
 * Project: Infrastructure Reporting & Tracking System
 * Description: Provides an endpoint used to verify database connectivity and
 *              confirm successful application-to-database communication.
 * Author: Sophina Nichols
 * Date Last Modified: 03/03/2026
 **************************************************************************/

package edu.loyno.cosca451;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

/** Mapped in WEB-INF/web.xml to /db-check */
public class DbCheckServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dbName = valueOrDefault("DB_NAME", "city_database");
        String user = valueOrDefault("DB_USER", "city_admin");
        String password = valueOrDefault("DB_PASSWORD", "city123");
        String host = valueOrDefault("DB_HOST", "localhost");
        String port = valueOrDefault("DB_PORT", "5433");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

        try {
            Class.forName("org.postgresql.Driver");

            try (Connection ignored = DriverManager.getConnection(jdbcUrl, user, password)) {
                response.getWriter().write("DB OK");
                return;
            }
        } catch (Exception firstException) {
            if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host)) {
                String dockerJdbcUrl = "jdbc:postgresql://db:5432/" + dbName;

                try {
                    Class.forName("org.postgresql.Driver");

                    try (Connection ignored = DriverManager.getConnection(dockerJdbcUrl, user, password)) {
                        response.getWriter().write("DB OK");
                        return;
                    }
                } catch (Exception secondException) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write(
                        "DB FAIL: tried " + jdbcUrl + " and " + dockerJdbcUrl +
                        " | first error: " + firstException.getMessage() +
                        " | second error: " + secondException.getMessage()
                    );
                    return;
                }
            }

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("DB FAIL: " + firstException.getMessage());
        }
    }

    private String valueOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
=======
/**************************************************************************
 * Filename: DbCheckServlet.java
 * Project: Infrastructure Reporting & Tracking System
 * Description: Provides an endpoint used to verify database connectivity and
 *              confirm successful application-to-database communication.
 * Author: Sophina Nichols
 * Edited By:
 * Hector Maes - 04/02/2026
 * Date Last Modified: 04/02/2026
 **************************************************************************/

package edu.loyno.cosca451;

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
        String dbName = valueOrDefault("DB_NAME", "city_database");
        String user = valueOrDefault("DB_USER", "city_admin");
        String password = valueOrDefault("DB_PASSWORD", "city123");
        String host = valueOrDefault("DB_HOST", "localhost");
        String port = valueOrDefault("DB_PORT", "5433");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

        try {
            Class.forName("org.postgresql.Driver");

            try (Connection ignored = DriverManager.getConnection(jdbcUrl, user, password)) {
                response.getWriter().write("DB OK");
                return;
            }
        } catch (Exception firstException) {
            if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host)) {
                String dockerJdbcUrl = "jdbc:postgresql://db:5432/" + dbName;

                try {
                    Class.forName("org.postgresql.Driver");

                    try (Connection ignored = DriverManager.getConnection(dockerJdbcUrl, user, password)) {
                        response.getWriter().write("DB OK");
                        return;
                    }
                } catch (Exception secondException) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write(
                        "DB FAIL: tried " + jdbcUrl + " and " + dockerJdbcUrl +
                        " | first error: " + firstException.getMessage() +
                        " | second error: " + secondException.getMessage()
                    );
                    return;
                }
            }

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("DB FAIL: " + firstException.getMessage());
        }
    }

    private String valueOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
>>>>>>> servlet-refactor
}