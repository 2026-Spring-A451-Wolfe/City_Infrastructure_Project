/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportUpdateRepository.java                                         *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: Handles all direct JDBC database queries for the report_images   *
 *              table. Responsible for inserting new image records and fetching  *
 *              all images associated with a given report. All queries must use  * 
 *              PreparedStatement.                                               *
 *              Called only by ReportService or ImageStorageService.             *
 * Author: Jana El-Khatib                                                       *
 * Date Last Modified: 03/13/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.repository;

import com.example.web.model.ReportImage;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class ReportImageRepository {
    private final DataSource dataSource;

    public ReportImageRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ReportImage save(ReportImage image) throws SQLException {
        String sql = "INSERT INTO report_images (report_id, image_url, file_path) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, image.getReportId());
            ps.setString(2, image.getImageUrl());
            ps.setString(3, image.getFilePath());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating image failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    image.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating image failed, no ID obtained.");
                }
            }
        }
        return image;
    }

    public Optional<ReportImage> findById(Integer id) throws SQLException {
        String sql = "SELECT id, report_id, image_url, file_path, uploaded_at FROM report_images WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReportImage image = new ReportImage();
                    image.setId(rs.getLong("id"));
                    image.setReportId(rs.getLong("report_id"));
                    image.setImageUrl(rs.getString("image_url"));
                    image.setFilePath(rs.getString("file_path"));
                    image.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
                    return Optional.of(image);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public void delete(ReportImage image) throws SQLException {
        String sql = "DELETE FROM report_images WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, image.getId());
            ps.executeUpdate();
        }
    }
}
