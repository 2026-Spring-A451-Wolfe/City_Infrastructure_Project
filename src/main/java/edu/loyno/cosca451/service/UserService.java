/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: UserService.java                                                  *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Contains business logic for user management operations         *
 *              available to admins. Handles fetching all users, retrieving    *
 *              a single user by ID, and deactivating a user account.          *
 *              References AuthService for structure.                          *
 * Author: Makayla Hairston                                                    *
 * Edited By:                                                                  *
 * Hector Maes - 04/02/2026                                                    *
 * Date Last Modified: 04/02/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.loyno.cosca451.service;

import edu.loyno.cosca451.model.User;
import edu.loyno.cosca451.db.UserDAO;

import java.util.List;

/*
 * UserService Responsibilites:
 * - Fetch all users or single user by ID
 * - Deactivate users
 * - Throws descriptive exceptions for invalid input or missing users
 */

public class UserService {

    // Repository used to query and update users in the database
    private final UserDAO userRepository;

    public UserService(UserDAO userRepository) {
        this.userRepository = userRepository;
    }

    // Fetch all users
    public List<User> getAllUsers() throws Exception {
        List<User> users = userRepository.findAll();

        if (users == null || users.isEmpty()) {
            throw new Exception("No users found");
        }

        return users;
    }

    // Fetch a single user by ID
    public User getUserById(Long userId) throws Exception {
        if (userId == null || userId <= 0) {
            throw new Exception("Invalid user ID");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
    }

    // Deactivate a user account
    public User deactivateUser(Long userId) throws Exception {
        if (userId == null || userId <= 0) {
            throw new Exception("Invalid user ID");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        if (!user.isActive()) {
            throw new Exception("User account is already deactivated");
        }

        user.setActive(false);

        return userRepository.save(user);
    }
}