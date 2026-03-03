package com.example.web.model;

public class DepartmentContact {

    private int id;
    private int departmentId;
    private String contactType;  // "phone", "email", "website"
    private String label;
    private String value;
    private boolean isEmergency;

    public DepartmentContact() {}

    public DepartmentContact(int id, int departmentId, String contactType,
                              String label, String value, boolean isEmergency) {
        this.id = id;
        this.departmentId = departmentId;
        this.contactType = contactType;
        this.label = label;
        this.value = value;
        this.isEmergency = isEmergency;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public boolean isEmergency() { return isEmergency; }
    public void setEmergency(boolean emergency) { isEmergency = emergency; }
}