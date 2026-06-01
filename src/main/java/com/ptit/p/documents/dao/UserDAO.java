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

    private boolean validateUser(User u) {
        if (u == null || u.getUsername() == null || u.getPassword() == null || u.getPhone() == null) {
            return false;
        }
        
        if (u.getUsername().length() < 5 || u.getUsername().length() > 20) return false;
        
        
        if (u.getPassword().length() < 6 || u.getPassword().length() > 32) return false;
        
        
        if (!u.getPhone().matches("\\d{10}")) return false;
        
        return true;
    }

    public User checkLogin(String username, String password) {
        User loginUser = new User();
        loginUser.setUsername(username);
        loginUser.setPassword(password);
        return checkLogin(loginUser);
    }

    public User checkLogin(User u) {
        if (u == null || u.getUsername() == null || u.getPassword() == null) {
            return null;
        }

        String sql = "SELECT * FROM tblUser WHERE username = ? AND password = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("ID"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("fullName"),
                            rs.getString("phone"),
                            rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> searchUser(String keyword) {
        List<User> result = new ArrayList<>();
        String key = (keyword == null) ? "" : keyword.trim();
        String sql = "SELECT * FROM tblUser WHERE username LIKE ? OR fullName LIKE ? OR phone LIKE ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String searchPattern = "%" + key + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new User(
                            rs.getInt("ID"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("fullName"),
                            rs.getString("phone"),
                            rs.getString("role")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean addUser(User u) {
        if (!validateUser(u)) {
            return false;
        }

        String checkSql = "SELECT ID FROM tblUser WHERE username = ?";
        try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setString(1, u.getUsername());
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO tblUser(username, password, fullName, phone, role) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User u) {
        if (!validateUser(u)) {
            return false;
        }

        String checkSql = "SELECT ID FROM tblUser WHERE username = ? AND ID != ?";
        try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setString(1, u.getUsername());
            checkPs.setInt(2, u.getId());
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "UPDATE tblUser SET username=?, password=?, fullName=?, phone=?, role=? WHERE ID=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getRole());
            ps.setInt(6, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(User u) {
        if (u == null) {
            return false;
        }

        String sql = "DELETE FROM tblUser WHERE ID=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
