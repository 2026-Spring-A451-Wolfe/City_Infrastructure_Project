package com.example.web.service;

import com.example.web.dto.DepartmentContactDTO;
import com.example.web.dto.DepartmentDTO;
import com.example.web.model.Department;
import com.example.web.model.DepartmentContact;
import com.example.web.repository.DepartmentRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<DepartmentDTO> getAllDepartments() throws SQLException {
        return departmentRepository.findAll()
                .stream()
                .map(this::toDTO)
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