package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookItemDAO extends DAO {
    public boolean updateStatus(int bookItemId, String status) {
        String sql = "UPDATE tblBookItem SET status = ? WHERE ID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, bookItemId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String getBookISBN(int bookItemId) {
        String sql = "SELECT tblBookISBN FROM tblBookItem WHERE ID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookItemId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("tblBookISBN");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
