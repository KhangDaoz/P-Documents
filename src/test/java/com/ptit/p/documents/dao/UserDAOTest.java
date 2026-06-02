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
        con.setAutoCommit(false); 
    }

    @AfterEach
    public void tearDown() throws SQLException {
        con.rollback(); 
        con.setAutoCommit(true);
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
        firstUser.setPassword("123456");
        firstUser.setFullName("Phan Đình Minh");
        firstUser.setPhone("0933444555");
        firstUser.setRole("manager");
        assertTrue(userDAO.addUser(firstUser));

        // Cố tình tạo 1 tài khoản nữa trùng username
        User duplicateUser = new User();
        duplicateUser.setUsername("minhpd"); 
        duplicateUser.setPassword("123456");
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
        testUser.setPassword("123456");
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
        targetUser.setPassword("123456");
        targetUser.setFullName("Mai Anh Tuấn");
        targetUser.setPhone("0977111222");
        targetUser.setRole("librarian");
        userDAO.addUser(targetUser);
        
        // 2. Tạo một user khác ngáng đường (Conflict)
        User conflictUser = new User();
        conflictUser.setUsername("hungnq");
        conflictUser.setPassword("123456");
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

    // --- KIỂM THỬ GIÁ TRỊ BIÊN 


    @Test
    public void testAddUser_EmptyOrNullFields() {
        User u = new User();
        
        // 1. User chưa set thông tin gì (Các trường mang giá trị Null)
        assertFalse(userDAO.addUser(u), "Tài khoản chứa giá trị Null ở các trường bắt buộc phải bị từ chối");

        // 2. User được set chuỗi rỗng (Empty strings)
        u.setUsername("");
        u.setPassword("");
        u.setFullName("");
        u.setPhone("");
        u.setRole("librarian");
        assertFalse(userDAO.addUser(u), "Tài khoản chứa chuỗi rỗng (Empty) phải bị từ chối");
    }

    @Test
    public void testAddUser_UsernameBVA() {
        User u = new User();
        u.setPassword("123456");
        u.setFullName("Test User");
        u.setPhone("0911222333");
        u.setRole("librarian");

        // 1. Min-1: 4 chars
        u.setUsername("abcd");
        assertFalse(userDAO.addUser(u), "Username < 5 ký tự phải bị từ chối");

        // 2. Min: 5 chars
        u.setUsername("abcde");
        assertTrue(userDAO.addUser(u), "Username = 5 ký tự phải hợp lệ");

        // 3. Max: 20 chars
        u.setUsername("12345678901234567890");
        assertTrue(userDAO.addUser(u), "Username = 20 ký tự phải hợp lệ");

        // 4. Max+1: 21 chars
        u.setUsername("123456789012345678901");
        assertFalse(userDAO.addUser(u), "Username > 20 ký tự phải bị từ chối");
    }

    @Test
    public void testAddUser_PasswordBVA() {
        User u = new User();
        u.setUsername("uniq_user");
        u.setFullName("Test User");
        u.setPhone("0911222333");
        u.setRole("librarian");

        // 1. Min-1: 5 chars
        u.setPassword("12345");
        assertFalse(userDAO.addUser(u), "Password < 6 ký tự phải bị từ chối");

        // 2. Min: 6 chars
        u.setPassword("123456");
        assertTrue(userDAO.addUser(u), "Password = 6 ký tự phải hợp lệ");

        // 3. Max: 32 chars
        u.setUsername("uniq_user2");
        u.setPassword("12345678901234567890123456789012");
        assertTrue(userDAO.addUser(u), "Password = 32 ký tự phải hợp lệ");

        // 4. Max+1: 33 chars
        u.setUsername("uniq_user3");
        u.setPassword("123456789012345678901234567890123");
        assertFalse(userDAO.addUser(u), "Password > 32 ký tự phải bị từ chối");
    }

    @Test
    public void testAddUser_PhoneBVA() {
        User u = new User();
        u.setUsername("phoneuser");
        u.setPassword("123456");
        u.setFullName("Test User");
        u.setRole("librarian");

        // 9 chars
        u.setPhone("091122233");
        assertFalse(userDAO.addUser(u), "Phone < 10 ký tự phải bị từ chối");

        // 11 chars
        u.setPhone("09112223334");
        assertFalse(userDAO.addUser(u), "Phone > 10 ký tự phải bị từ chối");

        // 10 chars, but invalid letters
        u.setPhone("091122233a");
        assertFalse(userDAO.addUser(u), "Phone có ký tự chữ phải bị từ chối");

        // exactly 10 digits
        u.setPhone("0911222333");
        assertTrue(userDAO.addUser(u), "Phone đúng 10 số phải hợp lệ");
    }
}
