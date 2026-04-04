<<<<<<<< HEAD:src/main/java/edu/loyno/cosca451/repository/ReportImageRepository.java
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

package edu.loyno.cosca451.repository;

import edu.loyno.cosca451.model.ReportImage;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

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

    public List<ReportImage> findByReportId(long reportId) throws SQLException {
        String sql = """
                SELECT id, report_id, image_url, file_path, uploaded_at
                FROM report_images
                WHERE report_id = ?
                ORDER BY uploaded_at ASC
                """;

        List<ReportImage> images = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reportId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportImage image = new ReportImage();
                    image.setId(rs.getLong("id"));
                    image.setReportId(rs.getLong("report_id"));
                    image.setImageUrl(rs.getString("image_url"));
                    image.setFilePath(rs.getString("file_path"));
                    image.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
                    images.add(image);
                }
            }
        }
        return images;
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
========
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportUpdateRepository.java                                         *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: Handles all direct JDBC database queries for the report_images   *
 *              table. Responsible for inserting new image records and fetching  *
 *              all images associated with a given report. All queries must use  * 
 *              PreparedStatement.                                               *
 *              Called only by ReportService or ImageStorageService.             *
 * Author: Jana El-Khatib                                                        *
 * Edited By:                                                                    *
 * Hector Maes - 04/02/2026                                                      *
 * Date Last Modified: 04/02/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.db;

import edu.loyno.cosca451.model.ReportImage;

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
>>>>>>>> servlet-refactor:src/main/java/edu/loyno/cosca451/db/ReportImageRepository.java
