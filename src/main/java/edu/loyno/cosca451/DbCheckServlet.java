/**************************************************************************
 * Filename: DbCheckServlet.java
 * Project: Infrastructure Reporting & Tracking System
 * Description: Provides an endpoint used to verify database connectivity and
 *              confirm successful application-to-database communication.
 * Author: Sophina Nichols
 * Date Last Modified: 03/30/2026
 **************************************************************************/

package com.example.web;

import com.example.web.util.DatabaseUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/db-check")
public class DbCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        try (Connection conn = DatabaseUtil.getDataSource().getConnection()) {
            var ps = conn.prepareStatement("SELECT COUNT(*) FROM users");
            var rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            response.getWriter().write("DB OK — users table found, " + count + " rows");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("DB FAIL: " + e.getMessage());
        }
    }
}