package com.example.web.model;

import java.time.LocalDateTime;
import java.util.List;

public class Department {

    private int id;
    private String name;
    private String jurisdiction;
    private String description;
    private LocalDateTime createdAt;
    private List<DepartmentContact> contacts;

    public Department() {}

    public Department(int id, String name, String jurisdiction,
                      String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.jurisdiction = jurisdiction;
        this.description = description;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<DepartmentContact> getContacts() { return contacts; }
    public void setContacts(List<DepartmentContact> contacts) { this.contacts = contacts; }
}