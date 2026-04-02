/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: DepartmentService.java                                    *
 * Project: NOLA Infrastructure Reporting & Tracking System            *
 * Description: Contains logic for retrieving, creating, and managing  *
 *              departments and their associated contact records.      *
 * Author: Sophina Nichols                                             *
 * Edited By:                                                          *
 * Hector Maes - 04/02/2026                                            *
 * Date Last Modified: 04/02/2026                                      *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.service;

import edu.loyno.cosca451.dto.DepartmentContactDTO;
import edu.loyno.cosca451.dto.DepartmentDTO;
import edu.loyno.cosca451.model.Department;
import edu.loyno.cosca451.model.DepartmentContact;
import edu.loyno.cosca451.db.DepartmentRepository;

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

    public DepartmentDTO getDepartmentById(long id) throws SQLException {
        return departmentRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    public List<DepartmentContactDTO> getContactsByDepartmentId(long id) throws SQLException {
        departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return departmentRepository.findContactsByDepartmentId(id)
                .stream()
                .map(this::toContactDTO)
                .collect(Collectors.toList());
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