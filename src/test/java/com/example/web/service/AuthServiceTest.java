/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Filename: AuthServiceTest.java                                              *
 * Project: NOLA Infrastructure Reporting & Tracking System                    *
 * Description: JUnit 5 test suite for AuthService. Covers user registration   *
 *              validation, login credential verification, duplicate username  *
 *              or email detection, password strength enforcement, BCrypt      *
 *              hashing, and JWT generation.                                   *
 * Author: Anderson Varela Suarez                                              *
 * Date Last Modified: 03/05/2026                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.example.web.service;

import com.example.web.dto.LoginRequest;
import com.example.web.dto.RegisterRequest;
import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import com.example.web.util.JwtUtil;
import com.example.web.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthService authService;
    private FakeUserRepository fakeUserRepository;

    @BeforeEach
    void setUp() {
        fakeUserRepository = new FakeUserRepository();
        authService = new AuthService(fakeUserRepository);
    }

    @Test
    void register_shouldCreateUser_whenRequestIsValid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ethan123");
        request.setEmailOrPhone("ethan@example.com");
        request.setPassword("Test@1234");

        User savedUser = authService.register(request);

        assertNotNull(savedUser);
        assertEquals("ethan123", savedUser.getUsername());
        assertEquals("ethan@example.com", savedUser.getEmailOrPhone());
        assertEquals("Citizen", savedUser.getRole());
        assertTrue(savedUser.isActive());
        assertNotNull(savedUser.getPasswordHash());
        assertNotEquals("Test@1234", savedUser.getPasswordHash());
        assertTrue(PasswordUtil.verifyPassword("Test@1234", savedUser.getPasswordHash()));
    }

    @Test
    void register_shouldThrow_whenUsernameTooShort() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ab");
        request.setEmailOrPhone("ethan@example.com");
        request.setPassword("Test@1234");

        Exception ex = assertThrows(Exception.class, () -> authService.register(request));
        assertEquals("Username must be at least 3 characters", ex.getMessage());
    }

    @Test
    void register_shouldThrow_whenEmailIsInvalid() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ethan123");
        request.setEmailOrPhone("not-an-email");
        request.setPassword("Test@1234");

        Exception ex = assertThrows(Exception.class, () -> authService.register(request));
        assertEquals("Invalid email address/phone number", ex.getMessage());
    }

    @Test
    void register_shouldThrow_whenPasswordTooShort() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ethan123");
        request.setEmailOrPhone("ethan@example.com");
        request.setPassword("T@1abc");

        Exception ex = assertThrows(Exception.class, () -> authService.register(request));
        assertEquals("Password must be at least 8 characters", ex.getMessage());
    }

    @Test
    void register_shouldThrow_whenPasswordMissingUppercase() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ethan123");
        request.setEmailOrPhone("ethan@example.com");
        request.setPassword("test@1234");

        Exception ex = assertThrows(Exception.class, () -> authService.register(request));
        assertEquals("Password must contain at least one uppercase letter", ex.getMessage());
    }

    @Test
    void register_shouldThrow_whenPasswordMissingNumber() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ethan123");
        request.setEmailOrPhone("ethan@example.com");
        request.setPassword("Test@abcd");

        Exception ex = assertThrows(Exception.class, () -> authService.register(request));
        assertEquals("Password must contain at least one number", ex.getMessage());
    }

    @Test
    void register_shouldThrow_whenPasswordMissingSpecialCharacter() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ethan123");
        request.setEmailOrPhone("ethan@example.com");
        request.setPassword("Test1234");

        Exception ex = assertThrows(Exception.class, () -> authService.register(request));
        assertEquals("Password must contain at least one special character", ex.getMessage());
    }

    @Test
    void register_shouldThrow_whenUsernameAlreadyExists() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("ethan123");
        existing.setEmailOrPhone("old@example.com");
        existing.setPasswordHash(PasswordUtil.hashPassword("Test@1234"));
        existing.setRole("Citizen");
        existing.setActive(true);

        fakeUserRepository.seedUser(existing);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("ethan123");
        request.setEmailOrPhone("new@example.com");
        request.setPassword("Test@1234");

        Exception ex = assertThrows(Exception.class, () -> authService.register(request));
        assertEquals("Username already taken", ex.getMessage());
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("someoneelse");
        existing.setEmailOrPhone("ethan@example.com");
        existing.setPasswordHash(PasswordUtil.hashPassword("Test@1234"));
        existing.setRole("Citizen");
        existing.setActive(true);

        fakeUserRepository.seedUser(existing);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("ethan123");
        request.setEmailOrPhone("ethan@example.com");
        request.setPassword("Test@1234");

        Exception ex = assertThrows(Exception.class, () -> authService.register(request));
        assertEquals("Email/phone already registered", ex.getMessage());
    }

    @Test
    void login_shouldReturnJwt_whenCredentialsAreValid() throws Exception {
        User existing = new User();
        existing.setId(7L);
        existing.setUsername("ethan123");
        existing.setEmailOrPhone("ethan@example.com");
        existing.setPasswordHash(PasswordUtil.hashPassword("Test@1234"));
        existing.setRole("Citizen");
        existing.setActive(true);

        fakeUserRepository.seedUser(existing);

        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("ethan@example.com");
        request.setPassword("Test@1234");

        String token = authService.login(request);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(7L, JwtUtil.getUserId(token));
        assertEquals("Citizen", JwtUtil.getRole(token));
    }

    @Test
    void login_shouldThrow_whenUserDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("missing@example.com");
        request.setPassword("Test@1234");

        Exception ex = assertThrows(Exception.class, () -> authService.login(request));
        assertEquals("Invalid email/phone or password", ex.getMessage());
    }

    @Test
    void login_shouldThrow_whenPasswordIsWrong() {
        User existing = new User();
        existing.setId(7L);
        existing.setUsername("ethan123");
        existing.setEmailOrPhone("ethan@example.com");
        existing.setPasswordHash(PasswordUtil.hashPassword("Test@1234"));
        existing.setRole("Citizen");
        existing.setActive(true);

        fakeUserRepository.seedUser(existing);

        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("ethan@example.com");
        request.setPassword("Wrong@1234");

        Exception ex = assertThrows(Exception.class, () -> authService.login(request));
        assertEquals("Invalid email/phone or password", ex.getMessage());
    }

    /**
     * Tiny fake repository so we can test AuthService logic without a real DB.
     */
    private static class FakeUserRepository extends UserRepository {

        private final java.util.List<User> users = new java.util.ArrayList<>();
        private long nextId = 1L;

        FakeUserRepository() {
            super(new DummyDataSource());
        }

        void seedUser(User user) {
            users.add(user);
            if (user.getId() >= nextId) {
                nextId = user.getId() + 1;
            }
        }

        @Override
        public Optional<User> findByEmailOrPhone(String emailOrPhone) {
            return users.stream()
                    .filter(u -> u.getEmailOrPhone().equals(emailOrPhone))
                    .findFirst();
        }

        @Override
        public Optional<User> findByUsername(String username) {
            return users.stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst();
        }

        @Override
        public User save(User user) {
            user.setId(nextId++);
            users.add(user);
            return user;
        }
    }

    /**
     * Only here because UserRepository requires a DataSource in its constructor.
     * The fake repository never actually uses it.
     */
    private static class DummyDataSource implements DataSource {

        @Override
        public java.sql.Connection getConnection() throws SQLException {
            throw new UnsupportedOperationException("Not used in tests");
        }

        @Override
        public java.sql.Connection getConnection(String username, String password) throws SQLException {
            throw new UnsupportedOperationException("Not used in tests");
        }

        @Override
        public java.io.PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        @Override
        public void setLogWriter(java.io.PrintWriter out) throws SQLException {
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public java.util.logging.Logger getParentLogger() {
            return java.util.logging.Logger.getGlobal();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new SQLException("Not a wrapper");
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }
    }
}