 /**************************************************************************
 * Filename: HealthServlet.java
 * Project: Infrastructure Reporting & Tracking System
 * Description: Provides a simple health check endpoint to verify that the
 *              application is running and responsive.
 * Author: Sophina Nichols
 * Date Last Modified: 03/03/2026
 **************************************************************************/

package com.example.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Mapped in WEB-INF/web.xml to /health */
public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("OK");
    }
}
