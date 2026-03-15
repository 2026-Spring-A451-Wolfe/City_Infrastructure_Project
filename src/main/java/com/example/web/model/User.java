/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: User.java                                                           *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: Represents a registered system user mapped to the users table,   *
 *              including authentication credentials and role assignment.        *
 * Author: Sophina Nichols                                                       *
 * Date Last Modified: 03/04/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.model;

import java.time.LocalDateTime;

public class User {
    private long id;
    private String username;
    private String email_or_phone;
    private String passwordHash;
    private String role;
    private boolean isActive;
    private LocalDateTime dateCreated;

    public User() {}

    public User(String username, String email_or_phone, String passwordHash, String role) {
        this.username = username;
        this.email_or_phone = email_or_phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isActive = true;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmailOrPhone() { return email_or_phone; }
    public void setEmailOrPhone(String email_or_phone) { this.email_or_phone = email_or_phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
}
