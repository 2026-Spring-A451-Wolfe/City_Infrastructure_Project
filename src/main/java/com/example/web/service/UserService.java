/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: UserService.java                                                  *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Contains business logic for user management operations         *
 *              available to admins. Handles fetching all users, retrieving    *
 *              a single user by ID, and deactivating a user account.          *
 *              References AuthService for structure.                          *
 * Author: Makayla Hairston, Ellie Carroll                                                   *
 * Date Last Modified: 03/27/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.service;

import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import com.example.web.util.PasswordUtil;

import java.util.List;
import java.util.regex.Pattern;

public class UserService {

    // Repository used to query and update users in the database
    private final UserRepository userRepository;

    // Regex patterns (converted from the JS)
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^ ]+@[^ ]+\\.[a-z]{2,3}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=(?:.*\\d){2,})(?=.*[!@#$%^&*(),.?\":{}|<>_\\-\\\\\\[\\]/+=~`]).+$");


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Helper method to validate user ID input
    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
    }

    // Helper method to validate user credentials to match frontend requirements (email and password)
    private void validateUserCredentials(String email, String password, String confirmPassword) {

        String emailValue = email.trim();
        String passwordValue = password.trim();
        String confirmPasswordValue = confirmPassword.trim();

        // Empty fields
        if (emailValue.isEmpty() || passwordValue.isEmpty() || confirmPasswordValue.isEmpty()) {
            throw new IllegalArgumentException("Please fill in all fields.");
        }

        //  Email format
        if (!EMAIL_PATTERN.matcher(emailValue).matches()) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }

        // Password length
        if (passwordValue.length() < 10) {
            throw new IllegalArgumentException("Password must be at least 10 characters long.");
        }

        // Password rules (2 numbers + 1 special char)
        if (!PASSWORD_PATTERN.matcher(passwordValue).matches()) {
            throw new IllegalArgumentException(
                "Password must include at least 2 numbers and 1 special character."
            );
        }

        // Match check
        if (!passwordValue.equals(confirmPasswordValue)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
    }

    // Register a new user (returns the created user or throws an exception if validation fails)
    public User registerUser(String email, String password, String confirmPassword) {

        // Run validation (same logic as frontend)
        validateUserCredentials(email, password, confirmPassword);
        // Trim inputs to remove leading/trailing whitespace
        email = email.trim();
        password = password.trim();
        confirmPassword = confirmPassword.trim();


        try {
             // Optional: check if email already exists
             if (userRepository.existsByEmailOrPhone(email)) {
                 throw new RuntimeException("Email is already in use.");
             }

        // Create and save user
        User user = new User();
        user.setUsername(email);
        user.setEmailOrPhone(email);
        
        user.setPasswordHash(PasswordUtil.hashPassword(password));

        user.setRole("Citizen"); // Must match allowed DB role values
        user.setActive(true);

        return userRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage());
        }

    }

    // Fetch all users
    public List<User> getAllUsers() throws Exception {
        List<User> users = userRepository.findAll();

        if (users == null || users.isEmpty()) {
            //throw new RuntimeException("No users found");
            return userRepository.findAll(); // Return empty list instead
        }

        return users;
    }

    // Fetch a single user by ID
    public User getUserById(Long userId) throws Exception {
        validateUserId(userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
    }

    // Deactivate a user account
    public User deactivateUser(Long userId) throws Exception {
        validateUserId(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("User account is already deactivated");
        }

        user.setActive(false);

        return userRepository.save(user);
    }
}