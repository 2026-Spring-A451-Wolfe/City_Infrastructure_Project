/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: UserRepository.java                                             *
 * Project: NOLA Infrastructure Reporting & Tracking System                  *
 * Description: Handles database operations for the users table, including   *
 *              user lookup and account management queries.                  *
 * Author: Sophina Nichols                                                   *            
 * Edited by:                                                                *
 * Hector Maes - 04/02/2026                                                  *
 * Date Last Modified: 04/02/2026                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.db;

import edu.loyno.cosca451.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * Changes Made:
 * - Renamed UserRepository -> UserDAO
 * - Moved to edu.loyno.cosca451.db package
 * - Updated import to match project structure
 * 
 * Note: All SQL logic remains unchanged 
 */

public class UserDAO {

    private final DataSource dataSource;

    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
        }

        return users;
    }

    public Optional<User> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<User> findByEmailOrPhone(String emailOrPhone) throws SQLException {
        String sql = "SELECT * FROM users WHERE email_or_phone = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emailOrPhone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
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
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
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

    public boolean existsByEmailOrPhone(String emailOrPhone) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email_or_phone = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emailOrPhone);

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
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isActive());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
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

    public User update(User user) throws SQLException {
        String sql = """
            UPDATE users SET is_active = ?
            WHERE id = ?
            RETURNING *
            """;
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, user.isActive());
            ps.setLong(2, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        throw new SQLException("Failed to update user");
    }
}