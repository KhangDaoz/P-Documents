package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends DAO {
    public User checkLogin(String username, String password) {
        String sql = "SELECT ID, username, password, fullName, phone, role FROM tblUser "
                   + "WHERE username = ? AND password = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                        resultSet.getInt("ID"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("fullName"),
                        resultSet.getString("phone"),
                        resultSet.getString("role")
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
