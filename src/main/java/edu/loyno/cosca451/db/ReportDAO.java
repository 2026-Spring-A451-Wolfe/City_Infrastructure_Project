/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportRepository.java                                             *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Handles all direct JDBC database queries for the reports,      *
 *              report_updates, and report_images tables. No business logic    *
 *              here — only raw SQL operations. Called only by ReportService.  *
 * Author: Carter Roberts, edited by Ethan DeLaRosa on 3/15                   *
 * Date Last Modified: 03/15/2026                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.db;

import edu.loyno.cosca451.model.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC driver not found.", e);
        }

        String jdbcUrl = valueOrDefault("JDBC_URL", "jdbc:postgresql://db:5432/city_database");
        String user = valueOrDefault("DB_USER", "city_admin");
        String password = valueOrDefault("DB_PASSWORD", "city123");

        return DriverManager.getConnection(jdbcUrl, user, password);
    }

    private String valueOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    public Report save(Report report) throws SQLException {
        String sql = """
            INSERT INTO reports
            (title, description, category, severity, latitude, longitude, status, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id, created_at, updated_at
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, report.getTitle());
            stmt.setString(2, report.getDescription());
            stmt.setString(3, report.getCategory());
            stmt.setString(4, report.getSeverity());
            stmt.setDouble(5, report.getLatitude());
            stmt.setDouble(6, report.getLongitude());
            stmt.setString(7, report.getStatus());
            stmt.setLong(8, report.getCreatedBy());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    report.setId(rs.getLong("id"));

                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        report.setCreatedAt(createdAt.toLocalDateTime());
                    }

                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        report.setUpdatedAt(updatedAt.toLocalDateTime());
                    }
                }
            }
        }

        return report;
    }

    public List<Report> findAll() throws SQLException {
        String sql = """
            SELECT id, title, description, category, severity,
                   latitude, longitude, status, created_by,
                   created_at, last_update_id, updated_at
            FROM reports
            ORDER BY created_at DESC
            """;

        List<Report> reports = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Report report = new Report();
                report.setId(rs.getLong("id"));
                report.setTitle(rs.getString("title"));
                report.setDescription(rs.getString("description"));
                report.setCategory(rs.getString("category"));
                report.setSeverity(rs.getString("severity"));
                report.setLatitude(rs.getDouble("latitude"));
                report.setLongitude(rs.getDouble("longitude"));
                report.setStatus(rs.getString("status"));
                report.setCreatedBy(rs.getLong("created_by"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    report.setCreatedAt(createdAt.toLocalDateTime());
                }

                long lastUpdateId = rs.getLong("last_update_id");
                if (!rs.wasNull()) {
                    report.setLastUpdateId(lastUpdateId);
                }

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    report.setUpdatedAt(updatedAt.toLocalDateTime());
                }

                reports.add(report);
            }
        }

        return reports;
    }
}