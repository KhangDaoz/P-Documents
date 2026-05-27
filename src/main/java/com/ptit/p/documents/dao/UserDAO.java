package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO {
    private static final List<User> listUsers = new ArrayList<>();
    private static int nextId = 5;

    static {
        // Initialize mock users (English names)
        listUsers.add(new User(1, "admin", "admin", "System Administrator", "0123456789", "admin"));
        listUsers.add(new User(2, "kien", "123", "Tran Trung Kien", "0987654321", "manager"));
        listUsers.add(new User(3, "khang", "123", "Dao Duy Khang", "0912345678", "staff"));
        listUsers.add(new User(4, "dung", "123", "Nguyen Tuan Dung", "0909090909", "staff"));
    }

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
        for (User user : listUsers) {
            if (user.getUsername().equals(u.getUsername()) && user.getPassword().equals(u.getPassword())) {
                // Return a new object to maintain encapsulation and mock DB retrieval
                return new User(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getFullName(),
                    user.getPhone(),
                    user.getRole()
                );
            }
        }
        return null;
    }

    /**
     * Searches users by keyword.
     * @param keyword Keyword (can be username, full name, or phone).
     * @return List of matching User objects.
     */
    public List<User> searchUser(String keyword) {
        List<User> result = new ArrayList<>();
        String key = (keyword == null) ? "" : keyword.trim().toLowerCase();
        for (User user : listUsers) {
            if (user.getUsername().toLowerCase().contains(key) ||
                user.getFullName().toLowerCase().contains(key) ||
                user.getPhone().toLowerCase().contains(key)) {
                result.add(new User(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getFullName(),
                    user.getPhone(),
                    user.getRole()
                ));
            }
        }
        return result;
    }

    /**
     * Adds a new user account to the database mock list.
     * @param u User object to add.
     * @return true if added successfully; false if username already exists.
     */
    public boolean addUser(User u) {
        if (u == null || u.getUsername() == null) {
            return false;
        }
        // Verify if username already exists
        for (User user : listUsers) {
            if (user.getUsername().equalsIgnoreCase(u.getUsername())) {
                return false;
            }
        }
        User newUser = new User(
            nextId++,
            u.getUsername(),
            u.getPassword(),
            u.getFullName(),
            u.getPhone(),
            u.getRole()
        );
        listUsers.add(newUser);
        return true;
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
        // Ensure new username doesn't conflict with another user
        for (User existing : listUsers) {
            if (existing.getId() != u.getId() && existing.getUsername().equalsIgnoreCase(u.getUsername())) {
                return false;
            }
        }
        for (int i = 0; i < listUsers.size(); i++) {
            if (listUsers.get(i).getId() == u.getId()) {
                User user = listUsers.get(i);
                user.setUsername(u.getUsername());
                user.setPassword(u.getPassword());
                user.setFullName(u.getFullName());
                user.setPhone(u.getPhone());
                user.setRole(u.getRole());
                return true;
            }
        }
        return false;
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
        for (int i = 0; i < listUsers.size(); i++) {
            if (listUsers.get(i).getId() == u.getId()) {
                listUsers.remove(i);
                return true;
            }
        }
        return false;
    }
}
