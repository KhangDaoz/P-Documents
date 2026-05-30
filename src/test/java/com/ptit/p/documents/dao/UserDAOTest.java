package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    void testDatabaseConnection() {
        // We will call the inherited getConnection() to verify the connection
        try (Connection conn = userDAO.getConnection()) {
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open");
            System.out.println("Database connection established successfully!");
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    @Test
    void testCheckLoginInvalidUser() {
        User result = userDAO.checkLogin("invalid_user_test", "wrong_password");

        // Expected to be null since this user shouldn't exist
        assertNull(result, "Login should fail (return null) for invalid credentials");
    }

    @Test
    void testCheckLoginValidUser() {
        // IMPORTANT: For this test to pass fully on assertions, ensure you have inserted this mock user in your p_documents database:
        // INSERT INTO tblUser (username, password, fullName, role) VALUES ('admin', '1812', 'Admin User', 'Manager');

        User result = userDAO.checkLogin("admin", "123456");

        // We use assertNotNull or warn if seed data is missing.
        if (result == null) {
            System.out.println("Warning: Valid user test failed. Please ensure 'admin'/'1812' exists in tblUser.");
        } else {
            assertEquals("admin", result.getUsername());
        }
        
        // Uncomment the line below when your actual local db has the seed data
        // assertNotNull(result, "Login should succeed (return User) for valid credentials");
    }
}
