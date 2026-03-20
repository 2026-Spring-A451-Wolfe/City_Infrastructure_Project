/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: ReportUpdateRepository.java                                       *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Handles all direct JDBC database queries for the               *
 *              report_updates table. Responsible for inserting new status     * 
 *              change records and fetching the full update history for a      *
 *              given report. All queries must use PreparedStatement.          *
 *              Called only by ReportService.                                  *
 * Author: Madeline Krehely                                                    *
 * Date Last Modified: 03/16/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
 
package com.example.web.repository;

import com.example.web.model.ReportUpdate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReportUpdateRepository {

    private final DataSource dataSource;

    public ReportUpdateRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ReportUpdate save(ReportUpdate update) throws SQLException { // new update
        String sql = """
                INSERT INTO report_updates
                (report_id, updater_id, old_status, new_status, department_id, comment)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, update.getReportID());
            ps.setLong(2, update.getUpdaterID());
            ps.setString(3, update.getOldStatus());
            ps.setString(4, update.getNewStatus());
            if (update.getDepartmentID() == 0) {
                ps.setNull(5, java.sql.Types.BIGINT);
            } else {
                ps.setLong(5, update.getDepartmentID());
            }
            ps.setString(6, update.getComment());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating report update failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    update.setID(generatedKeys.getLong(1));
                } 
                else {
                    throw new SQLException("Creating report update failed, no ID obtained.");
                }
            }
        }
        return update;
    }

    public Optional<ReportUpdate> findLatestByReportID(long reportID) throws SQLException {
        String sql = """
                SELECT id, report_id, updater_id, old_status, new_status, 
                    department_id, comment, updated_at
                FROM report_updates
                WHERE report_id = ?
                ORDER BY updated_at DESC
                LIMIT 1
            """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, reportID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ReportUpdate update = mapRow(rs);
                    return Optional.of(update);
                }
                else {
                    return Optional.empty();
                }
            }
        }
    }
    
    public List<ReportUpdate> findByReportID(long reportID) throws SQLException {
        String sql = """
                SELECT id, report_id, updater_id, old_status, new_status, 
                    department_id, comment, updated_at
                FROM report_updates
                WHERE report_id = ?
                ORDER BY updated_at ASC
            """;

        List<ReportUpdate> updates = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, reportID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        updates.add(mapRow(rs));
                    }
                }
            }
        return updates;
    }

    private ReportUpdate mapRow(ResultSet rs) throws SQLException {
        ReportUpdate update = new ReportUpdate();
        
        update.setID(rs.getLong("id"));
        update.setReportID(rs.getLong("report_id"));
        update.setUpdaterID(rs.getLong("updater_id"));
        update.setOldStatus(rs.getString("old_status"));
        update.setNewStatus(rs.getString("new_status"));
        update.setDepartmentID(rs.getLong("department_id"));
        update.setComment(rs.getString("comment"));
        update.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return update;
    }
}