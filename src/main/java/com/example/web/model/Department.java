/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: Department.java                                                     *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: Model class that mirrors the departments table in the database,  *
 *              were each instance represents one city department.             *
 * Author: Sophina Nichols                                                       *
 * Date Last Modified: 03/03/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.model;

import java.time.LocalDateTime;
import java.util.List;

public class Department {

    private long id;
    private String name;
    private String jurisdiction;
    private String description;
    private LocalDateTime createdAt;
    private List<DepartmentContact> contacts;

    public Department() {}

    public Department(long id, String name, String jurisdiction,
                      String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.jurisdiction = jurisdiction;
        this.description = description;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

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