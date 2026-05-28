package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO {

    public UserDAO() {
        super();
    }

    /**
     * Xác thực thông tin đăng nhập.
     * @param u Đối tượng chứa tài khoản và mật khẩu cần xác thực.
     * @return Đối tượng User với đầy đủ thông tin nếu đúng; ngược lại trả về null.
     */
    public User checkLogin(User u) {
        if (u == null || u.getUsername() == null || u.getPassword() == null) {
            return null;
        }
        User user = null;
        String sql = "SELECT * FROM tblUser WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(
                    rs.getInt("ID"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("fullName"),
                    rs.getString("phone"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Tìm kiếm người dùng theo từ khóa.
     * @param keyword Từ khóa (có thể là tên đăng nhập, họ tên hoặc số điện thoại).
     * @return Danh sách các đối tượng User phù hợp.
     */
    public List<User> searchUser(String keyword) {
        List<User> result = new ArrayList<>();
        String key = (keyword == null) ? "" : keyword.trim();
        String sql = "SELECT * FROM tblUser WHERE username LIKE ? OR fullName LIKE ? OR phone LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            String searchPattern = "%" + key + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new User(
                    rs.getInt("ID"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("fullName"),
                    rs.getString("phone"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Thêm tài khoản người dùng mới vào cơ sở dữ liệu.
     * @param u Đối tượng User cần thêm.
     * @return true nếu thêm thành công; false nếu tên đăng nhập đã tồn tại.
     */
    public boolean addUser(User u) {
        if (u == null || u.getUsername() == null) {
            return false;
        }
        // Kiểm tra xem tên đăng nhập đã tồn tại chưa
        String checkSql = "SELECT ID FROM tblUser WHERE username = ?";
        try {
            PreparedStatement psCheck = con.prepareStatement(checkSql);
            psCheck.setString(1, u.getUsername());
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) {
                return false; // Tên đăng nhập đã tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO tblUser(username, password, fullName, phone, role) VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getRole());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin người dùng.
     * @param u Đối tượng User chứa thông tin mới (trùng ID).
     * @return true nếu cập nhật thành công; false nếu không tìm thấy ID người dùng.
     */
    public boolean updateUser(User u) {
        if (u == null) {
            return false;
        }
        // Kiểm tra xem tên đăng nhập mới có trùng với người dùng khác không
        String checkSql = "SELECT ID FROM tblUser WHERE username = ? AND ID != ?";
        try {
            PreparedStatement psCheck = con.prepareStatement(checkSql);
            psCheck.setString(1, u.getUsername());
            psCheck.setInt(2, u.getId());
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) {
                return false; // Trùng tên đăng nhập
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE tblUser SET username=?, password=?, fullName=?, phone=?, role=? WHERE ID=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getRole());
            ps.setInt(6, u.getId());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tài khoản người dùng khỏi hệ thống.
     * @param u Đối tượng User cần xóa (chỉ cần chứa ID mục tiêu).
     * @return true nếu xóa thành công; false nếu không tìm thấy ID.
     */
    public boolean deleteUser(User u) {
        if (u == null) {
            return false;
        }
        String sql = "DELETE FROM tblUser WHERE ID=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, u.getId());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
