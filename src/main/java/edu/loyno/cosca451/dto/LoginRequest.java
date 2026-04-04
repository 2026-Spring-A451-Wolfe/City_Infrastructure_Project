<<<<<<< HEAD
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: LoginRequest.java                                               *
 * Project: NOLA Infrastructure Reporting & Tracking System                  *
 * Description: Data Transfer Object used to capture user login credentials  *
 *              submitted to the authentication endpoint.                    *
 * Author: Sophina Nichols                                                   *
 * Date Last Modified: 03/04/2026                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.dto;

/* LoginRequest is a DTO that represents the JSON body sent by the client to
 * POST /api/auth/login. This class contains no logic, it only carries data.
 */

public class LoginRequest {
    private String email_or_phone;
    private String password;

    public LoginRequest() {}

    public String getEmailOrPhone() { return email_or_phone; }
    public void setEmailOrPhone(String email_or_phone) { this.email_or_phone = email_or_phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
}
=======
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: LoginRequest.java                                               *
 * Project: NOLA Infrastructure Reporting & Tracking System                  *
 * Description: Data Transfer Object used to capture user login credentials  *
 *              submitted to the authentication endpoint.                    *
 * Author: Sophina Nichols                                                   *                                    
 * Edited By:                                                                *
 * Hector Maes - 04/02/2026                                                  *
 * Date Last Modified: 04/02/2026                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.dto;

/* LoginRequest is a DTO that represents the JSON body sent by the client to
 * POST /api/auth/login. This class contains no logic, it only carries data.
 */

public class LoginRequest {
    private String email_or_phone;
    private String password;

    public LoginRequest() {}

    public String getEmailOrPhone() { return email_or_phone; }
    public void setEmailOrPhone(String email_or_phone) { this.email_or_phone = email_or_phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
}
>>>>>>> servlet-refactor
