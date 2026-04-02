/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: DepartmentContactDTO.java                                   *
 * Project: NOLA Infrastructure Reporting & Tracking System              *
 * Description: Data Transfer Object used to safely transfer department  *
 *              contact information between the backend and client.      *
 * Author: Sophina Nichols                                               *
 * Edited By:                                                            *
 * Hector Maes - 04/02/2026                                              *
 * Date Last Modified: 04/02/2026                                        *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.dto;

/* DepartmentContactDTO is a DTO that represents one contact method for a 
 * department in an API response.
 */

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
    
    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public boolean isEmergency() { return isEmergency; }
    public void setEmergency(boolean emergency) { isEmergency = emergency; }
}