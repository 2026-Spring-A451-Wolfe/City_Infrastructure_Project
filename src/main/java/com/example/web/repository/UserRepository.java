/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: UserRepository.java                                             *
 * Project: NOLA Infrastructure Reporting & Tracking System                  *
 * Description: Handles database operations for the users table, including   *
 *              user lookup and account management queries.                  *
 * Author: Sophina Nichols                                                   *
 * Date Last Modified: 03/04/2026                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.repository;

import com.example.web.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class UserRepository {

    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<User> findByEmailOrPhone(String emailophone) throws SQLException {
        String sql = "SELECT * FROM users WHERE email_or_phone = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, emailophone);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return Optional.of(mapRow(rs));
                }
            }
        return Optional.empty();
    }

    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
    }

    public boolean existsByEmailOrPhone(String emailophone) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email_or_phone = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, emailophone);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
    }

    public User save(User user) throws SQLException {
        String sql = """
            INSERT INTO users (username, email_or_phone, password_hash, role, is_active)
            VALUES (?, ?, ?, ?, ?)
            RETURNING *
            """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmailOrPhone());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole());
            ps.setBoolean(6, user.isActive());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        throw new SQLException("Failed to save user");
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmailOrPhone(rs.getString("email_or_phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("is_active"));
        user.setDateCreated(rs.getTimestamp("date_created").toLocalDateTime());
        return user;
    }
}