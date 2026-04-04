/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: PasswordUtil.java                                               *
 * Project: NOLA Infrastructure Reporting & Tracking System                  *
 * Description: Provides secure password hashing and verification utilities  *
 *              to protect user credentials.                                 *
 * Author: Sophina Nichols                                                   *
 * Edited By:                                                                *
 * Hector Maes - 04/02/2026                                                  *
 * Date Last Modified: 04/02/2026                                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.util;

import org.mindrot.jbcrypt.BCrypt;

/* PasswordUtil is a utility class for securely handling user passwords.
 * Passwords are NEVER stored as plain text in this system. Passwords are 
 * hashed using BCrypt before being saved to the database. 
 */

public class PasswordUtil {
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
