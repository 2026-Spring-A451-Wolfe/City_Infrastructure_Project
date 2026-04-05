/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: JwtAuthFilter.java                                                      *
 * Project: NOLA Infrastructure Reporting & Tracking System                          *
 * Description: Servlet filter that intercepts all incoming HTTP requests and        *
 *              validates the JWT token from the Authorization header before         *
 *              allowing access to protected endpoints. Extracts and verifies the.   *
 *              token using JwtUtil, rejects requests with missing or invalid tokens,*
 *              and attaches the decoded user claims (userId, username, role) to the *
 *              request as attributes for downstream use by controllers. Public.     *
 *              routes are bypassed entirely. Must be registered in web.xml          *
 * Author: Sophina Nichols                                                           *
 * Edited By:                                                                        *
 * Hector Maes - 04/02/2026                                                          *
 * Date Last Modified: 04/02/2026                                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.config;

import edu.loyno.cosca451.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JwtAuthFilter implements Filter {

    private final ObjectMapper mapper = new ObjectMapper();

    //public endpoints that do not require authentication
    private static final Map<String, String[]> PUBLIC_ROUTES = Map.of(
        "/auth/register", new String[]{"POST"},
        "/auth/login",    new String[]{"POST"},
        "/api/departments",   new String[]{"GET"},
        "/api/reports",       new String[]{"GET"}
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path   = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        if (isPublicRoute(path, method)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(httpResponse, "Missing or malformed Authorization header.");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = JwtUtil.validateToken(token);

            httpRequest.setAttribute("userId",   claims.get("userId",   Long.class));
            httpRequest.setAttribute("username", claims.get("username", String.class));
            httpRequest.setAttribute("role",     claims.get("role",     String.class));

            chain.doFilter(request, response);

        } catch (Exception e) {
            // Catches expired tokens, tampered signatures, malformed JWTs, etc.
            sendUnauthorized(httpResponse, "Invalid or expired token.");
        }
    }

    @Override
    public void destroy() {
    }
    /**
     * Returns true if the request path + method combination is public.
     * Also allows all GET requests to /api/reports/{id} and /api/departments/{id}
     * since single report and department views are publicly accessible.
     */
    private boolean isPublicRoute(String path, String method) {
        String[] allowedMethods = PUBLIC_ROUTES.get(path);
        if (allowedMethods != null) {
            for (String m : allowedMethods) {
                if (m.equalsIgnoreCase(method)) return true;
            }
        }

        if ("GET".equalsIgnoreCase(method)) {
            if (path.matches("/api/reports/\\d+"))              return true;
            if (path.matches("/api/reports/\\d+/updates"))      return true;
            if (path.matches("/api/departments/\\d+"))          return true;
            if (path.matches("/api/departments/\\d+/contacts")) return true;
        }

        return false;
    }

    // Writes a 401 Unauthorized JSON response and stops the filter chain.
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
            mapper.writeValueAsString(Map.of(
                "error",   "Unauthorized",
                "message", message
            ))
        );
    }
}