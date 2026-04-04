/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: DepartmentContact.java                                                  *
 * Project: NOLA Infrastructure Reporting & Tracking System                          *
 * Description: Model class that mirrors the department_contacts table in database,  *
 *              where each instance represents one contact method for a department.  *
 *              A single department can have multiple DepartmentContact entries.     *
 * Author: Sophina Nichols                                                           *
 * Edited By:                                                                        *
 * Hector Maes - 04/02/2026                                                          * 
 * Date Last Modified: 04/02/2026                                                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.model;

public class DepartmentContact {

    private long id;
    private long departmentId;
    private String contactType;
    private String label;
    private String value;
    private boolean isEmergency;

    public DepartmentContact() {}

    public DepartmentContact(long id, long departmentId, String contactType,
                              String label, String value, boolean isEmergency) {
        this.id = id;
        this.departmentId = departmentId;
        this.contactType = contactType;
        this.label = label;
        this.value = value;
        this.isEmergency = isEmergency;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getDepartmentId() { return departmentId; }
    public void setDepartmentId(long departmentId) { this.departmentId = departmentId; }

    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public boolean isEmergency() { return isEmergency; }
    public void setEmergency(boolean emergency) { isEmergency = emergency; }
}