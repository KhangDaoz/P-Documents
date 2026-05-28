package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private UserDAO userDAO;
    private Connection con;

    @BeforeEach
    public void setUp() throws SQLException {
        userDAO = new UserDAO();
        con = userDAO.con; 
        
        if (con != null) {
            con.setAutoCommit(false); 
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (con != null) {
            con.rollback(); 
            con.setAutoCommit(true);
            con.close(); 
        }
    }

    // --- CHỨC NĂNG ĐĂNG NHẬP (checkLogin) ---

    @Test
    public void testCheckLoginStandard() {
        // Chuẩn bị dữ liệu 
        User testUser = new User();
        testUser.setUsername("test_login_std");
        testUser.setPassword("123456");
        testUser.setFullName("Login Standard User");
        testUser.setPhone("0111222333");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        // Chạy test
        User loginRequest = new User();
        loginRequest.setUsername("test_login_std");
        loginRequest.setPassword("123456");

        User result = userDAO.checkLogin(loginRequest);
        assertNotNull(result);
        assertEquals("test_login_std", result.getUsername());
        assertEquals("Login Standard User", result.getFullName());
        assertEquals("librarian", result.getRole());
    }

    @Test
    public void testCheckLoginException() {
        // Chuẩn bị dữ liệu
        User testUser = new User();
        testUser.setUsername("test_login_err");
        testUser.setPassword("123456");
        testUser.setFullName("Login Error User");
        testUser.setPhone("0111222333");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        // Cố tình truyền sai password
        User loginRequest = new User();
        loginRequest.setUsername("test_login_err");
        loginRequest.setPassword("wrongpassword");
        
        User result = userDAO.checkLogin(loginRequest);
        assertNull(result);
    }

    // --- CHỨC NĂNG TÌM KIẾM (searchUser) ---

    @Test
    public void testSearchUserStandard() {
        // Chuẩn bị dữ liệu
        User testUser = new User();
        testUser.setUsername("test_search");
        testUser.setPassword("pass");
        testUser.setFullName("Nguyen Van Search");
        testUser.setPhone("0999000111");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        // Chạy test
        List<User> results = userDAO.searchUser("Van Search");
        assertFalse(results.isEmpty());
        
        // Kiểm tra xem kết quả có chứa user mình vừa tạo không
        boolean found = false;
        for (User u : results) {
            if (u.getUsername().equals("test_search")) {
                assertEquals("Nguyen Van Search", u.getFullName());
                found = true;
                break;
            }
        }
        assertTrue(found, "Phải tìm thấy user vừa tạo ra.");
    }

    @Test
    public void testSearchUserNotFoundException() {
        List<User> results = userDAO.searchUser("nonexistentuser_random_123");
        assertTrue(results.isEmpty());
    }

    // --- CHỨC NĂNG THÊM TÀI KHOẢN (addUser) ---

    @Test
    public void testAddUserStandard() {
        User newUser = new User();
        newUser.setUsername("test_add_new");
        newUser.setPassword("password");
        newUser.setFullName("Add New Name");
        newUser.setPhone("0999888777");
        newUser.setRole("librarian");

        boolean addSuccess = userDAO.addUser(newUser);
        assertTrue(addSuccess);

        // Xác nhận lại xem thêm được chưa
        List<User> searchResults = userDAO.searchUser("test_add_new");
        assertFalse(searchResults.isEmpty());
        assertEquals("Add New Name", searchResults.get(0).getFullName());
    }

    @Test
    public void testAddUserException() {
        // Tạo 1 tài khoản ban đầu
        User firstUser = new User();
        firstUser.setUsername("test_duplicate"); 
        firstUser.setPassword("pass1");
        firstUser.setFullName("First User");
        firstUser.setPhone("111");
        firstUser.setRole("manager");
        assertTrue(userDAO.addUser(firstUser));

        // Cố tình tạo 1 tài khoản nữa trùng username
        User duplicateUser = new User();
        duplicateUser.setUsername("test_duplicate"); 
        duplicateUser.setPassword("pass2");
        duplicateUser.setFullName("Duplicate User");
        duplicateUser.setPhone("222");
        duplicateUser.setRole("librarian");

        boolean addDuplicateSuccess = userDAO.addUser(duplicateUser);
        assertFalse(addDuplicateSuccess);
    }

    // --- CHỨC NĂNG CẬP NHẬT TÀI KHOẢN (updateUser) ---

    @Test
    public void testUpdateUserStandard() {
        // 1. Chuẩn bị dữ liệu ban đầu
        User testUser = new User();
        testUser.setUsername("test_update");
        testUser.setPassword("pass");
        testUser.setFullName("Old Name");
        testUser.setPhone("0000");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        // Lấy user ra để lấy đúng ID được DB sinh ra
        List<User> searchResults = userDAO.searchUser("test_update");
        assertFalse(searchResults.isEmpty());
        User userToUpdate = searchResults.get(0);

        // 2. Chỉnh sửa
        userToUpdate.setFullName("Updated Name");
        userToUpdate.setPhone("9999");

        boolean updateSuccess = userDAO.updateUser(userToUpdate);
        assertTrue(updateSuccess);

        // 3. Lấy lại từ DB để đối chiếu
        List<User> updatedResults = userDAO.searchUser("test_update");
        assertFalse(updatedResults.isEmpty());
        assertEquals("Updated Name", updatedResults.get(0).getFullName());
        assertEquals("9999", updatedResults.get(0).getPhone());
    }

    @Test
    public void testUpdateUserException() {
        // 1. Tạo user muốn đổi (Target)
        User targetUser = new User();
        targetUser.setUsername("test_update_fail");
        targetUser.setPassword("pass");
        targetUser.setFullName("Target User");
        targetUser.setPhone("111");
        targetUser.setRole("librarian");
        userDAO.addUser(targetUser);
        
        // 2. Tạo một user khác ngáng đường (Conflict)
        User conflictUser = new User();
        conflictUser.setUsername("test_conflict");
        conflictUser.setPassword("pass");
        conflictUser.setFullName("Conflict User");
        conflictUser.setPhone("222");
        conflictUser.setRole("librarian");
        userDAO.addUser(conflictUser);

        // 3. Lấy Target ra 
        List<User> searchResults = userDAO.searchUser("test_update_fail");
        assertFalse(searchResults.isEmpty());
        User userToUpdate = searchResults.get(0);

        // 4. Cố tình đổi username của Target trùng với tên của Conflict
        userToUpdate.setUsername("test_conflict");

        boolean updateDuplicateSuccess = userDAO.updateUser(userToUpdate);
        assertFalse(updateDuplicateSuccess);
    }

    // --- CHỨC NĂNG XÓA TÀI KHOẢN (deleteUser) ---

    @Test
    public void testDeleteUserStandard() {
        // 1. Tạo một user tạm
        User toDelete = new User();
        toDelete.setUsername("test_todelete");
        toDelete.setPassword("password");
        toDelete.setFullName("Delete Me");
        toDelete.setPhone("1234");
        toDelete.setRole("librarian");
        userDAO.addUser(toDelete);
        
        // 2. Lấy nó ra từ DB để có ID
        List<User> searchBefore = userDAO.searchUser("test_todelete");
        assertFalse(searchBefore.isEmpty());
        User createdUser = searchBefore.get(0);

        // 3. Thực hiện hành động xóa
        boolean deleteSuccess = userDAO.deleteUser(createdUser);
        assertTrue(deleteSuccess);

        // 4. Xác minh là đã xóa sạch
        List<User> searchAfter = userDAO.searchUser("test_todelete");
        assertTrue(searchAfter.isEmpty());
    }
}
