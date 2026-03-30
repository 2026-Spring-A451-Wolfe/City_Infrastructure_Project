/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: UserService.java                                                  *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: Contains business logic for user management operations         *
 *              available to admins. Handles fetching all users, retrieving    *
 *              a single user by ID, and deactivating a user account.          *
 *              References AuthService for structure.                          *
 * Author: Makayla Hairston                                                    *
 * Date Last Modified: 03/05/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.service;

import com.example.web.model.User;
import com.example.web.repository.UserRepository;

import java.util.List;

public class UserService {

    // Repository used to query and update users in the database
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
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

        return userRepository.update(user);
    }
}
