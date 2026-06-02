package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BookItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookItemDAO extends DAO {
    public boolean addBookItem(BookItem item) {
        String sql = "INSERT INTO tblBookItem (status, tblBookISBN) VALUES (?, ?)";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setString(1, item.getStatus() != null ? item.getStatus() : "good");
            ps.setString(2, item.getBookISBN());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBookItem(String isbn) {
        String sql = "DELETE FROM tblBookItem WHERE tblBookISBN = ?";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setString(1, isbn);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int bookItemId, String status) {
        String sql = "UPDATE tblBookItem SET status = ? WHERE ID = ?";
        try (PreparedStatement statement = getCon().prepareStatement(sql)) {
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
        try (PreparedStatement statement = getCon().prepareStatement(sql)) {
            statement.setInt(1, bookItemId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("tblBookISBN");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
