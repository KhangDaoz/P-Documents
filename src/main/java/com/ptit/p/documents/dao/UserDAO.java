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
     * Verifies login credentials.
     * @param u Entity containing username and password to verify.
     * @return User object with full details if credentials are correct; null otherwise.
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
     * Searches users by keyword.
     * @param keyword Keyword (can be username, full name, or phone).
     * @return List of matching User objects.
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
     * Adds a new user account to the database.
     * @param u User object to add.
     * @return true if added successfully; false if username already exists.
     */
    public boolean addUser(User u) {
        if (u == null || u.getUsername() == null) {
            return false;
        }
        // Check if username already exists
        String checkSql = "SELECT ID FROM tblUser WHERE username = ?";
        try {
            PreparedStatement psCheck = con.prepareStatement(checkSql);
            psCheck.setString(1, u.getUsername());
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) {
                return false; // Username exists
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
     * Updates user information.
     * @param u User object containing updated information (matching ID).
     * @return true if updated successfully; false if target user ID is not found.
     */
    public boolean updateUser(User u) {
        if (u == null) {
            return false;
        }
        // Check if new username conflicts with another user
        String checkSql = "SELECT ID FROM tblUser WHERE username = ? AND ID != ?";
        try {
            PreparedStatement psCheck = con.prepareStatement(checkSql);
            psCheck.setString(1, u.getUsername());
            psCheck.setInt(2, u.getId());
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) {
                return false; // Conflict
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
     * Deletes a user account from the system.
     * @param u User object to delete (or just containing the target ID).
     * @return true if deleted successfully; false if ID is not found.
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
