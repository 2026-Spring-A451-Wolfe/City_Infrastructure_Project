/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: RegisterRequest.java                                                *
 * Project: NOLA Infrastructure Reporting & Tracking System                      *
 * Description: Data Transfer Object used to capture user registration details   *
 *              before creating a new system account.                            *
 * Author: Sophina Nichols                                                       *
 * Edited By:                                                                    *
 * Hector Maes - 04/02/2026                                                      *
 * Date Last Modified: 04/02/2026                                                *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.dto;

/* RegisterRequest is a DTO that represents the JSON body sent by the client to
 * POST /api/auth/register. This class contains no logic, it only carries data.
 */

public class RegisterRequest {
    private String username;
    private String email_or_phone;
    private String password;

    public RegisterRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmailOrPhone() { return email_or_phone; }
    public void setEmailOrPhone(String email_or_phone) { this.email_or_phone = email_or_phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
