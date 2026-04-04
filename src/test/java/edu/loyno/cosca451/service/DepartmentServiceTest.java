/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: DepartmentServiceTest.java                                        *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Tests for DepartmentService, verifying department retrieval,   *
 *              DTO conversion, and error handling logic.                      *
 * Author: Sophina Nichols                                                     *
 * Date Last Modified: 03/03/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.service;

import edu.loyno.cosca451.dto.DepartmentContactDTO;
import edu.loyno.cosca451.dto.DepartmentDTO;
import edu.loyno.cosca451.model.Department;
import edu.loyno.cosca451.model.DepartmentContact;
import edu.loyno.cosca451.db.DepartmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/* These tests use a manual "fake" (stub) implementation of DepartmentRepository
 * instead of a real database connection.
 * Tests make sure that:
 *                - getAllDepartments() returns correct DTOs
 *                - getAllDepartments() returns empty list when no departments exist
 *                - getDepartmentById() returns correct DTO for valid ID
 *                - getDepartmentById() throws RuntimeException for invalid ID
 *                - Contact fields are correctly mapped to DTOs
 *                - Emergency contacts are included in DTO
 *                - Department with no contacts returns empty contacts list
 */

class DepartmentServiceTest {

    // Service we are testing
    private DepartmentService departmentService;

    private StubDepartmentRepository stubRepository;
    /* StubDepartmentRepository is a fake implementation of DepartmentRepository
     * used only for testing. It does not connect to any database.
    */

    @BeforeEach
    void setUp() {
        stubRepository = new StubDepartmentRepository();
        departmentService = new DepartmentService(stubRepository);
    }

    /* getAllDepartments() Tests */ 

    @Test
    // Expected Outcome: should return a list of DepartmentDTOs matching the departments returned by the repository
    void getAllDepartments_returnsDTOList() throws SQLException {
        stubRepository.departments = List.of(buildDepartment(1L, "NOLA-311", "Orleans Parish",
                "Non-emergency city services", buildContact(1L, 1L, "phone", "Call NOLA-311", "311", false)));
        List<DepartmentDTO> result = departmentService.getAllDepartments();
        assertEquals(1, result.size());
        DepartmentDTO dto = result.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("NOLA-311", dto.getName());
        assertEquals("Orleans Parish", dto.getJurisdiction());
        assertEquals("Non-emergency city services", dto.getDescription());
    }

    @Test
    // Expected Outcome: should return an empty list when there are no departments in the database
    void getAllDepartments_emptyList_returnsEmptyList() throws SQLException {
        stubRepository.departments = Collections.emptyList();
        List<DepartmentDTO> result = departmentService.getAllDepartments();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    // Expected Outcome: should return multiple DTOs when multiple departments exist
    void getAllDepartments_multipleDepartments_returnsAllDTOs() throws SQLException {
        stubRepository.departments = List.of(
            buildDepartment(1L, "NOLA-311", "Orleans Parish", "Non-emergency services",
                buildContact(1L, 1L, "phone", "Call NOLA-311", "311", false)),
            buildDepartment(2L, "DPW Main Division", "Orleans Parish", "Public works",
                buildContact(2L, 2L, "phone", "Call DPW", "(504) 658-8000", false))
        );
        List<DepartmentDTO> result = departmentService.getAllDepartments();
        assertEquals(2, result.size());
        assertEquals("NOLA-311", result.get(0).getName());
        assertEquals("DPW Main Division", result.get(1).getName());
    }

    /* getDepartmentById() Tests */

    @Test
    // Expected Outcome: should return the correct DTO when a department with the given ID exists
    void getDepartmentById_validId_returnsDTO() throws SQLException {
        stubRepository.departmentById = Optional.of(
            buildDepartment(1L, "NOLA-311", "Orleans Parish", "Non-emergency city services",
                buildContact(1L, 1L, "phone", "Call NOLA-311", "311", false))
        );
        DepartmentDTO result = departmentService.getDepartmentById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("NOLA-311", result.getName());
    }

    @Test
    // Expected Outcome: should throw a RuntimeException when no department exists with the given ID
    void getDepartmentById_invalidId_throwsRuntimeException() {
        stubRepository.departmentById = Optional.empty();
        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> departmentService.getDepartmentById(999L));
        assertTrue(ex.getMessage().contains("Department not found with id: 999"));
    }

    /* Contact DTO Mapping Tests */

    @Test
    // Expected Outcome: contacts should be correctly converted from DepartmentContact models to DepartmentContactDTOs with all fields mapped
    void getAllDepartments_contactFieldsCorrectlyMapped() throws SQLException {
        stubRepository.departments = List.of(
            buildDepartment(1L, "NOLA-311", "Orleans Parish", "Non-emergency services",
                buildContact(1L, 1L, "email", "Email NOLA-311", "311@nola.gov", false))
        );
        DepartmentDTO result = departmentService.getAllDepartments().get(0);
        DepartmentContactDTO contact = result.getContacts().get(0);
        assertEquals("email", contact.getContactType());
        assertEquals("Email NOLA-311", contact.getLabel());
        assertEquals("311@nola.gov", contact.getValue());
        assertFalse(contact.isEmergency());
    }

    @Test
    // Expected Outcome: emergency contacts should have isEmergency set to true in the DTO
    void getAllDepartments_emergencyContact_isEmergencyTrue() throws SQLException {
        stubRepository.departments = List.of(
            buildDepartment(5L, "Sewerage and Water Board", "Orleans Parish",
                "Manages water and drainage systems",
                buildContact(11L, 5L, "phone", "Emergency Flood Line", "504-529-2837", true))
        );
        DepartmentDTO result = departmentService.getAllDepartments().get(0);
        DepartmentContactDTO contact = result.getContacts().get(0);
        assertTrue(contact.isEmergency());
        assertEquals("Emergency Flood Line", contact.getLabel());
        assertEquals("504-529-2837", contact.getValue());
    }

    @Test 
    // Expected Outcome: a department with no contacts should return a DTO with an empty contacts list, not null
    void getAllDepartments_noContacts_returnsEmptyContactsList() throws SQLException {
        Department dept = new Department(1L, "NOLA-311", "Orleans Parish",
                "Non-emergency services", LocalDateTime.now());
        dept.setContacts(Collections.emptyList());
        stubRepository.departments = List.of(dept);
        DepartmentDTO result = departmentService.getAllDepartments().get(0);
        assertNotNull(result.getContacts());
        assertTrue(result.getContacts().isEmpty());
    }

    @Test
    // Expected Outcome: should correctly map contact fields for a single department lookup
    void getDepartmentById_contactsIncludedInDTO() throws SQLException {
        stubRepository.departmentById = Optional.of(
            buildDepartment(2L, "DPW Main Division", "Orleans Parish", "Public works",
                buildContact(2L, 2L, "email", "Email DPW", "dpw@nola.gov", false))
        );
        DepartmentDTO result = departmentService.getDepartmentById(2L);
        assertEquals(1, result.getContacts().size());
        assertEquals("dpw@nola.gov", result.getContacts().get(0).getValue());
    }

    // Builds a Department object with one contact for use in tests
    private Department buildDepartment(long id, String name, String jurisdiction,
                                        String description, DepartmentContact contact) {
        Department dept = new Department(id, name, jurisdiction, description, LocalDateTime.now());
        dept.setContacts(List.of(contact));
        return dept;
    }

    //Builds a DepartmentContact object for use in tests
    private DepartmentContact buildContact(long id, long departmentId, String contactType,
                                            String label, String value, boolean isEmergency) {
        return new DepartmentContact(id, departmentId, contactType, label, value, isEmergency);
    }

    private static class StubDepartmentRepository extends DepartmentRepository {
        // Set these fields to control what the stub returns
        List<Department> departments = Collections.emptyList();
        Optional<Department> departmentById = Optional.empty();

        StubDepartmentRepository() {
            super(null);
        }

        @Override
        public List<Department> findAll() throws SQLException {
            return departments;
        }

        @Override
        public Optional<Department> findById(long id) throws SQLException {
            return departmentById;
        }
    }
}