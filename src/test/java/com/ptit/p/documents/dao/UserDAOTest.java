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
        con = userDAO.getCon(); 
        
        if (con != null) {
            con.setAutoCommit(false); 
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (con != null) {
            con.rollback(); 
            con.setAutoCommit(true);
        }
    }

    @Test
    void testDatabaseConnection() {
        // We will call the inherited getConnection() to verify the connection
        try {
            Connection conn = userDAO.getConnection();
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open");
            System.out.println("Database connection established successfully!");
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    // --- CHỨC NĂNG ĐĂNG NHẬP (checkLogin) ---

    @Test
    public void testCheckLoginStandard() {
        // Chuẩn bị dữ liệu 
        User testUser = new User();
        testUser.setUsername("hoangnd");
        testUser.setPassword("123456");
        testUser.setFullName("Nguyễn Đình Hoàng");
        testUser.setPhone("0911222333");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        // Chạy test
        User loginRequest = new User();
        loginRequest.setUsername("hoangnd");
        loginRequest.setPassword("123456");

        User result = userDAO.checkLogin(loginRequest);
        assertNotNull(result);
        assertEquals("hoangnd", result.getUsername());
        assertEquals("Nguyễn Đình Hoàng", result.getFullName());
        assertEquals("librarian", result.getRole());
    }

    @Test
    public void testCheckLoginException() {
        // Chuẩn bị dữ liệu
        User testUser = new User();
        testUser.setUsername("hoangnd_fake");
        testUser.setPassword("123456");
        testUser.setFullName("Tài Khoản Lỗi");
        testUser.setPhone("0911222334");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        // Cố tình truyền sai password
        User loginRequest = new User();
        loginRequest.setUsername("hoangnd_fake");
        loginRequest.setPassword("wrongpassword");
        
        User result = userDAO.checkLogin(loginRequest);
        assertNull(result);
    }

    // --- CHỨC NĂNG TÌM KIẾM (searchUser) ---

    @Test
    public void testSearchUserStandard() {
        // Chuẩn bị dữ liệu
        User testUser = new User();
        testUser.setUsername("tuanvq");
        testUser.setPassword("123456");
        testUser.setFullName("Vũ Quang Tuấn");
        testUser.setPhone("0988777666");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        // Chạy test
        List<User> results = userDAO.searchUser("Quang Tuấn");
        assertFalse(results.isEmpty());
        
        // Kiểm tra xem kết quả có chứa user mình vừa tạo không
        boolean found = false;
        for (User u : results) {
            if (u.getUsername().equals("tuanvq")) {
                assertEquals("Vũ Quang Tuấn", u.getFullName());
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
        newUser.setUsername("linhpt");
        newUser.setPassword("password");
        newUser.setFullName("Phạm Thùy Linh");
        newUser.setPhone("0911222333");
        newUser.setRole("librarian");

        boolean addSuccess = userDAO.addUser(newUser);
        assertTrue(addSuccess);

        // Xác nhận lại xem thêm được chưa
        List<User> searchResults = userDAO.searchUser("linhpt");
        assertFalse(searchResults.isEmpty());
        assertEquals("Phạm Thùy Linh", searchResults.get(0).getFullName());
    }

    @Test
    public void testAddUserException() {
        // Tạo 1 tài khoản ban đầu
        User firstUser = new User();
        firstUser.setUsername("minhpd"); 
        firstUser.setPassword("pass1");
        firstUser.setFullName("Phan Đình Minh");
        firstUser.setPhone("0933444555");
        firstUser.setRole("manager");
        assertTrue(userDAO.addUser(firstUser));

        // Cố tình tạo 1 tài khoản nữa trùng username
        User duplicateUser = new User();
        duplicateUser.setUsername("minhpd"); 
        duplicateUser.setPassword("pass2");
        duplicateUser.setFullName("Phan Đình Minh 2");
        duplicateUser.setPhone("0933444666");
        duplicateUser.setRole("librarian");

        boolean addDuplicateSuccess = userDAO.addUser(duplicateUser);
        assertFalse(addDuplicateSuccess);
    }

    // --- CHỨC NĂNG CẬP NHẬT TÀI KHOẢN (updateUser) ---

    @Test
    public void testUpdateUserStandard() {
        // 1. Chuẩn bị dữ liệu ban đầu
        User testUser = new User();
        testUser.setUsername("quangnd");
        testUser.setPassword("pass");
        testUser.setFullName("Nguyễn Đức Quang");
        testUser.setPhone("0966777888");
        testUser.setRole("librarian");
        userDAO.addUser(testUser);

        // Lấy user ra để lấy đúng ID được DB sinh ra
        List<User> searchResults = userDAO.searchUser("quangnd");
        assertFalse(searchResults.isEmpty());
        User userToUpdate = searchResults.get(0);

        // 2. Chỉnh sửa
        userToUpdate.setFullName("Nguyễn Đức Quang S");
        userToUpdate.setPhone("0966777999");

        boolean updateSuccess = userDAO.updateUser(userToUpdate);
        assertTrue(updateSuccess);

        // 3. Lấy lại từ DB để đối chiếu
        List<User> updatedResults = userDAO.searchUser("quangnd");
        assertFalse(updatedResults.isEmpty());
        assertEquals("Nguyễn Đức Quang S", updatedResults.get(0).getFullName());
        assertEquals("0966777999", updatedResults.get(0).getPhone());
    }

    @Test
    public void testUpdateUserException() {
        // 1. Tạo user muốn đổi (Target)
        User targetUser = new User();
        targetUser.setUsername("tuanma");
        targetUser.setPassword("pass");
        targetUser.setFullName("Mai Anh Tuấn");
        targetUser.setPhone("0977111222");
        targetUser.setRole("librarian");
        userDAO.addUser(targetUser);
        
        // 2. Tạo một user khác ngáng đường (Conflict)
        User conflictUser = new User();
        conflictUser.setUsername("hungnq");
        conflictUser.setPassword("pass");
        conflictUser.setFullName("Nguyễn Quang Hưng");
        conflictUser.setPhone("0977333444");
        conflictUser.setRole("librarian");
        userDAO.addUser(conflictUser);

        // 3. Lấy Target ra 
        List<User> searchResults = userDAO.searchUser("tuanma");
        assertFalse(searchResults.isEmpty());
        User userToUpdate = searchResults.get(0);

        // 4. Cố tình đổi username của Target trùng với tên của Conflict
        userToUpdate.setUsername("hungnq");

        boolean updateDuplicateSuccess = userDAO.updateUser(userToUpdate);
        assertFalse(updateDuplicateSuccess);
    }

    // --- CHỨC NĂNG XÓA TÀI KHOẢN (deleteUser) ---

    @Test
    public void testDeleteUserStandard() {
        // 1. Tạo một user tạm
        User toDelete = new User();
        toDelete.setUsername("khoihd");
        toDelete.setPassword("123456");
        toDelete.setFullName("Hoàng Đình Khôi");
        toDelete.setPhone("0944555666");
        toDelete.setRole("librarian");
        userDAO.addUser(toDelete);
        
        // 2. Lấy nó ra từ DB để có ID
        List<User> searchBefore = userDAO.searchUser("khoihd");
        assertFalse(searchBefore.isEmpty());
        User createdUser = searchBefore.get(0);

        // 3. Thực hiện hành động xóa
        boolean deleteSuccess = userDAO.deleteUser(createdUser);
        assertTrue(deleteSuccess);

        // 4. Xác minh là đã xóa sạch
        List<User> searchAfter = userDAO.searchUser("khoihd");
        assertTrue(searchAfter.isEmpty());
    }
}
