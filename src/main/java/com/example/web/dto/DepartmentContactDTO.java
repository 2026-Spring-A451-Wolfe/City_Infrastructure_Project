package com.example.web.dto;

public class DepartmentContactDTO {

    private String contactType;
    private String label;
    private String value;
    private boolean isEmergency;

    public DepartmentContactDTO() {}

    public DepartmentContactDTO(String contactType, String label, String value,
                                boolean isEmergency) {
        this.contactType = contactType;
        this.label = label;
        this.value = value;
        this.isEmergency = isEmergency;
    }

    // Getters and Setters
    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public boolean isEmergency() { return isEmergency; }
    public void setEmergency(boolean emergency) { isEmergency = emergency; }
}