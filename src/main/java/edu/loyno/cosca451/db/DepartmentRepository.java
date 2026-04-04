/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: DepartmentRepository.java                                     *
 * Project: NOLA Infrastructure Reporting & Tracking System                *
 * Description: Handles database operations for the departments table,     *
 *              including retrieval and persistence of department records. *
 * Author: Sophina Nichols                                                 *
 * Edited By:                                                              *
 * Hector Maes - 04/02/2026                                                *
 * Date Last Modified: 04/02/2026                                          *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.db;

import edu.loyno.cosca451.model.Department;
import edu.loyno.cosca451.model.DepartmentContact;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* DepartmentRepository handles all direct database access for departments.
 * Each method opens its own connection from the DataSource pool, executes a 
 * query, and then closes the connection automatically. Departments are managed
 * directly in the database by admins.
 */

public class DepartmentRepository {

    private final DataSource dataSource;

    public DepartmentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Department> findAll() throws SQLException {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Department dept = mapRow(rs);
                dept.setContacts(findContactsByDepartmentId(dept.getId()));
                departments.add(dept);
            }
        }
        return departments;
    }

    public Optional<Department> findById(long id) throws SQLException {
        String sql = "SELECT * FROM departments WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Department dept = mapRow(rs);
                    dept.setContacts(findContactsByDepartmentId(dept.getId()));
                    return Optional.of(dept);
                }
            }
        }
        return Optional.empty();
    }

    public List<DepartmentContact> findContactsByDepartmentId(long departmentId) throws SQLException {
        List<DepartmentContact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM department_contacts WHERE department_id = ? ORDER BY is_emergency DESC, id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapContactRow(rs));
                }
            }
        }
        return contacts;
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        return new Department(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("jurisdiction"),
            rs.getString("description"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    private DepartmentContact mapContactRow(ResultSet rs) throws SQLException {
        return new DepartmentContact(
            rs.getLong("id"),
            rs.getLong("department_id"),
            rs.getString("contact_type"),
            rs.getString("label"),
            rs.getString("value"),
            rs.getBoolean("is_emergency")
        );
    }
}