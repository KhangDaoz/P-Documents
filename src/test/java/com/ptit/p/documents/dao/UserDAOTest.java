package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    public void testCheckLoginSuccess() {
        User loginUser = new User();
        loginUser.setUsername("admin");
        loginUser.setPassword("admin");

        User result = userDAO.checkLogin(loginUser);
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("System Administrator", result.getFullName());
        assertEquals("admin", result.getRole());
    }

    @Test
    public void testCheckLoginFailure() {
        User loginUser = new User();
        loginUser.setUsername("admin");
        loginUser.setPassword("wrongpassword");

        User result = userDAO.checkLogin(loginUser);
        assertNull(result);
    }

    @Test
    public void testSearchUser() {
        List<User> results = userDAO.searchUser("kien");
        assertFalse(results.isEmpty());
        User kien = results.get(0);
        assertEquals("kien", kien.getUsername());
        assertEquals("Tran Trung Kien", kien.getFullName());
    }

    @Test
    public void testSearchUserNotFound() {
        List<User> results = userDAO.searchUser("nonexistentuser123");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testAddAndSearchUser() {
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("password");
        newUser.setFullName("Test User Name");
        newUser.setPhone("0999888777");
        newUser.setRole("staff");

        // Thêm mới tài khoản
        boolean addSuccess = userDAO.addUser(newUser);
        assertTrue(addSuccess);

        // Kiểm tra xem đã tìm thấy tài khoản mới chưa
        List<User> searchResults = userDAO.searchUser("testuser");
        assertEquals(1, searchResults.size());
        assertEquals("Test User Name", searchResults.get(0).getFullName());

        // Kiểm tra trùng username
        User duplicateUser = new User();
        duplicateUser.setUsername("testuser");
        duplicateUser.setPassword("pass");
        duplicateUser.setFullName("Duplicate");
        duplicateUser.setPhone("123");
        duplicateUser.setRole("manager");

        boolean addDuplicateSuccess = userDAO.addUser(duplicateUser);
        assertFalse(addDuplicateSuccess);
    }

    @Test
    public void testUpdateUser() {
        // Tìm tài khoản kien (ID = 2)
        List<User> searchResults = userDAO.searchUser("kien");
        assertFalse(searchResults.isEmpty());
        User kien = searchResults.get(0);

        // Thay đổi họ tên, số điện thoại và tên đăng nhập
        kien.setUsername("kien_new");
        kien.setFullName("Tran Trung Kien - Updated");
        kien.setPhone("0000000000");

        boolean updateSuccess = userDAO.updateUser(kien);
        assertTrue(updateSuccess);

        // Tìm lại xem thông tin đã cập nhật chưa
        List<User> updatedResults = userDAO.searchUser("kien_new");
        assertFalse(updatedResults.isEmpty());
        assertEquals("Tran Trung Kien - Updated", updatedResults.get(0).getFullName());
        assertEquals("0000000000", updatedResults.get(0).getPhone());
        assertEquals("kien_new", updatedResults.get(0).getUsername());

        // Kiểm tra không được trùng tên đăng nhập với tài khoản khác (vd: admin)
        kien.setUsername("admin");
        boolean updateDuplicateSuccess = userDAO.updateUser(kien);
        assertFalse(updateDuplicateSuccess);
    }

    @Test
    public void testDeleteUser() {
        // Tạo tài khoản mới để xoá
        User toDelete = new User();
        toDelete.setUsername("todelete");
        toDelete.setPassword("password");
        toDelete.setFullName("Delete Me");
        toDelete.setPhone("1234");
        toDelete.setRole("staff");

        userDAO.addUser(toDelete);
        
        // Tìm để lấy ID thực tế của user vừa tạo
        List<User> searchBefore = userDAO.searchUser("todelete");
        assertFalse(searchBefore.isEmpty());
        User createdUser = searchBefore.get(0);

        // Thực hiện xoá
        boolean deleteSuccess = userDAO.deleteUser(createdUser);
        assertTrue(deleteSuccess);

        // Kiểm tra tìm kiếm lại xem đã mất chưa
        List<User> searchAfter = userDAO.searchUser("todelete");
        assertTrue(searchAfter.isEmpty());
    }
}
