package com.example.web.dto;

import java.util.List;

public class DepartmentDTO {

    private int id;
    private String name;
    private String jurisdiction;
    private String description;
    private List<DepartmentContactDTO> contacts;

    public DepartmentDTO() {}

    public DepartmentDTO(int id, String name, String jurisdiction, String description,
                         List<DepartmentContactDTO> contacts) {
        this.id = id;
        this.name = name;
        this.jurisdiction = jurisdiction;
        this.description = description;
        this.contacts = contacts;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<DepartmentContactDTO> getContacts() { return contacts; }
    public void setContacts(List<DepartmentContactDTO> contacts) { this.contacts = contacts; }
}