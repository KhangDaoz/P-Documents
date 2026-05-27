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
        loginUser.setPassword("123"); 

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
        List<User> results = userDAO.searchUser("thangnt");
        assertFalse(results.isEmpty());
        User thang = results.get(0);
        assertEquals("thangnt", thang.getUsername());
        assertEquals("Nguyen Tien Thang", thang.getFullName());
    }

    @Test
    public void testSearchUserNotFound() {
        List<User> results = userDAO.searchUser("nonexistentuser123");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testAddAndSearchUser() {
        User newUser = new User();
        newUser.setUsername("testuser_add");
        newUser.setPassword("password");
        newUser.setFullName("Test User Name");
        newUser.setPhone("0999888777");
        newUser.setRole("librarian");

        
        List<User> cleanList = userDAO.searchUser("testuser_add");
        if (!cleanList.isEmpty()) userDAO.deleteUser(cleanList.get(0));

        // Thêm mới tài khoản
        boolean addSuccess = userDAO.addUser(newUser);
        assertTrue(addSuccess);

        // Kiểm tra xem đã tìm thấy tài khoản mới chưa
        List<User> searchResults = userDAO.searchUser("testuser_add");
        assertEquals(1, searchResults.size());
        assertEquals("Test User Name", searchResults.get(0).getFullName());

        // Kiểm tra trùng username
        User duplicateUser = new User();
        duplicateUser.setUsername("testuser_add");
        duplicateUser.setPassword("pass");
        duplicateUser.setFullName("Duplicate");
        duplicateUser.setPhone("123");
        duplicateUser.setRole("manager");

        boolean addDuplicateSuccess = userDAO.addUser(duplicateUser);
        assertFalse(addDuplicateSuccess);

        // Cleanup
        userDAO.deleteUser(searchResults.get(0));
    }

    @Test
    public void testUpdateUser() {
        // Tìm tài khoản thangnt để cập nhật
        List<User> searchResults = userDAO.searchUser("thangnt");
        assertFalse(searchResults.isEmpty());
        User thang = searchResults.get(0);

        String originalFullName = thang.getFullName();
        String originalPhone = thang.getPhone();

        try {
            // Thay đổi họ tên, số điện thoại
            thang.setFullName("Nguyen Tien Thang - Updated");
            thang.setPhone("0000000000");

            boolean updateSuccess = userDAO.updateUser(thang);
            assertTrue(updateSuccess);

            // Tìm lại xem thông tin đã cập nhật chưa
            List<User> updatedResults = userDAO.searchUser("thangnt");
            assertFalse(updatedResults.isEmpty());
            assertEquals("Nguyen Tien Thang - Updated", updatedResults.get(0).getFullName());
            assertEquals("0000000000", updatedResults.get(0).getPhone());

            // Kiểm tra không được trùng tên đăng nhập với tài khoản khác (vd: admin)
            thang.setUsername("admin");
            boolean updateDuplicateSuccess = userDAO.updateUser(thang);
            assertFalse(updateDuplicateSuccess);
        } finally {
            // Restore back to original to keep DB state clean for next test runs
            thang.setUsername("thangnt");
            thang.setFullName(originalFullName);
            thang.setPhone(originalPhone);
            userDAO.updateUser(thang);
        }
    }

    @Test
    public void testDeleteUser() {
        // Tạo tài khoản mới để xoá
        User toDelete = new User();
        toDelete.setUsername("test_todelete");
        toDelete.setPassword("password");
        toDelete.setFullName("Delete Me");
        toDelete.setPhone("1234");
        toDelete.setRole("librarian");

        // Clean if exists
        List<User> cleanList = userDAO.searchUser("test_todelete");
        if (!cleanList.isEmpty()) userDAO.deleteUser(cleanList.get(0));

        userDAO.addUser(toDelete);
        
        // Tìm để lấy ID thực tế của user vừa tạo
        List<User> searchBefore = userDAO.searchUser("test_todelete");
        assertFalse(searchBefore.isEmpty());
        User createdUser = searchBefore.get(0);

        // Thực hiện xoá
        boolean deleteSuccess = userDAO.deleteUser(createdUser);
        assertTrue(deleteSuccess);

        // Kiểm tra tìm kiếm lại xem đã mất chưa
        List<User> searchAfter = userDAO.searchUser("test_todelete");
        assertTrue(searchAfter.isEmpty());
    }
}
