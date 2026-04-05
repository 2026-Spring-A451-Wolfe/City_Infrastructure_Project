/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: DepartmentDTO.java                                                    *
 * Project: NOLA Infrastructure Reporting & Tracking System                        *
 * Description: Data Transfer Object used to represent department data in API      *
 *              responses and prevent direct exposure of the Department entity.    *
 * Author: Sophina Nichols                                                         *
 * Edited By:                                                                      *
 * Hector Maes - 04/02/2026                                                        *
 * Date Last Modified: 04/02/2026                                                  *                                               *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.dto;

import java.util.List;

/* DepartmentDTO is a DTO that represents what the API sends back to the client when 
 * a department is requested. Each DepartmentDTO includes a list of DepartmentContactDTOs 
 * so the frontend receives all contact info for a department in one response.
 */

public class DepartmentDTO {

    private long id;
    private String name;
    private String jurisdiction;
    private String description;
    private List<DepartmentContactDTO> contacts;

    public DepartmentDTO() {}

    public DepartmentDTO(long id, String name, String jurisdiction, String description,
                         List<DepartmentContactDTO> contacts) {
        this.id = id;
        this.name = name;
        this.jurisdiction = jurisdiction;
        this.description = description;
        this.contacts = contacts;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<DepartmentContactDTO> getContacts() { return contacts; }
    public void setContacts(List<DepartmentContactDTO> contacts) { this.contacts = contacts; }
}