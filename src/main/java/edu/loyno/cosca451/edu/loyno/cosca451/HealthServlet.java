 /**************************************************************************
 * Filename: HealthServlet.java
 * Project: Infrastructure Reporting & Tracking System
 * Description: Provides a simple health check endpoint to verify that the
 *              application is running and responsive.
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

@WebServlet("/health")
public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("OK");
    }
}
