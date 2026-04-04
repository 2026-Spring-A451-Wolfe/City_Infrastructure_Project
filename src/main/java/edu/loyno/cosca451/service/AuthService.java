/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Filename: AuthService.java                                                      *
 * Project: NOLA Infrastructure Reporting & Tracking System                        *
 * Description: Handles registration logic (password validation & BCrypt hashing)  *
 *              and login logic (BCrypt verification & JWT token generation).      *
 * Author: Sophina Nichols                                                         *
 * Edited by:                                                                      *
 * Hector Maes - 04/02/2026                                                        *
 * Date Last Modified: 04/02/2026                                                  *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.service;

import edu.loyno.cosca451.dto.LoginRequest;
import edu.loyno.cosca451.dto.RegisterRequest;
import edu.loyno.cosca451.model.User;
import edu.loyno.cosca451.db.UserDAO;
import edu.loyno.cosca451.util.JwtUtil;
import edu.loyno.cosca451.util.PasswordUtil;

import java.sql.SQLException;

/* 
 * Changes made:
 * - Replaced UserRepository -> UserDao
 * - Updated imports to match edu.loyno.cosca451 package structure
 * - Updated constructor to accept UserDAO
 * 
 * Functionality remains:
 * - Registration validate (username, email/phone, password strength)
 * - Check for duplicates in DB
 * - Hash passwords
 * - Verify passwords
 * - Generate JWT on login
 */

public class AuthService {

    // Repository used to query and save users in the database
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Registration 
    public User register(RegisterRequest request) throws Exception, SQLException {

        if (request.getUsername() == null || request.getUsername().trim().length() < 3) {
            throw new Exception("Username must be at least 3 characters");
        }

        if (request.getEmailOrPhone() == null || !request.getEmailOrPhone().contains("@")) {
            throw new Exception("Invalid email address/phone number");
        }

        String password = request.getPassword();
        if (password == null || password.length() < 8) {
            throw new Exception("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new Exception("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new Exception("Password must contain at least one number");
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            throw new Exception("Password must contain at least one special character");
        }

        if (userDAO.findByUsername(request.getUsername()).isPresent()) {
            throw new Exception("Username already taken");
        }
        if (userDAO.findByEmailOrPhone(request.getEmailOrPhone()).isPresent()) {
            throw new Exception("Email/phone already registered");
        }

        String hashedPassword = PasswordUtil.hashPassword(request.getPassword());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmailOrPhone(request.getEmailOrPhone());
        newUser.setPasswordHash(hashedPassword);
        newUser.setRole("Citizen");     // New accounts default to role "Citizen"
        newUser.setActive(true);

        return userDAO.save(newUser);
    }

    // Authentication
    public String login(LoginRequest request) throws Exception, SQLException {

        User user = userDAO.findByEmailOrPhone(request.getEmailOrPhone())
                .orElseThrow(() -> new Exception("Invalid email/phone or password"));

        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            throw new Exception("Invalid email/phone or password");
        }
        
        return JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }
}
