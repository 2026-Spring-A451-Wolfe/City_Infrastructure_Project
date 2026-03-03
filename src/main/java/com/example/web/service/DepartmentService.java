/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: DepartmentService.java                                    *
 * Project: NOLA Infrastructure Reporting & Tracking System            *
 * Description: Contains logic for retrieving, creating, and managing  *
 *              departments and their associated contact records.      *
 * Author: Sophina Nichols                                             *
 * Date Last Modified: 03/03/2026                                      *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.service;

import com.example.web.dto.DepartmentContactDTO;
import com.example.web.dto.DepartmentDTO;
import com.example.web.model.Department;
import com.example.web.model.DepartmentContact;
import com.example.web.repository.DepartmentRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


/* DepartmentService contains the logic layer for departments.
 * It sits between DepartmentController and DepartmentRepository, and its
 * main task is conversting Department model objects into DepartmentDTOs 
 * before they are serialized and sent to the client.
 */

public class DepartmentService {
    // Repository used to query the departments and department_contacts tables
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<DepartmentDTO> getAllDepartments() throws SQLException {
        return departmentRepository.findAll()
                .stream()
                .map(this::toDTO) // convert each Department to a DepartmentDTO
                .collect(Collectors.toList());
    }

    public DepartmentDTO getDepartmentById(int id) throws SQLException {
        return departmentRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    private DepartmentDTO toDTO(Department d) {
        List<DepartmentContactDTO> contactDTOs = d.getContacts()
                .stream()
                .map(this::toContactDTO)
                .collect(Collectors.toList());

        return new DepartmentDTO(
            d.getId(),
            d.getName(),
            d.getJurisdiction(),
            d.getDescription(),
            contactDTOs
        );
    }

    private DepartmentContactDTO toContactDTO(DepartmentContact c) {
        return new DepartmentContactDTO(
            c.getContactType(),
            c.getLabel(),
            c.getValue(),
            c.isEmergency()
        );
    }
}