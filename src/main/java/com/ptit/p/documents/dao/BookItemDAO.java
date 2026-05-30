package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BookItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookItemDAO extends DAO {

    /**
     * Thêm một bản sách vật lý mới vào CSDL.
     * Trạng thái mặc định là "good".
     */
    public boolean addBookItem(BookItem item) {
        String sql = "INSERT INTO tblBookItem (status, tblBookISBN) VALUES (?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getStatus() != null ? item.getStatus() : "good");
            ps.setString(2, item.getBookISBN());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa tất cả bản sách vật lý theo ISBN.
     * Phải gọi trước khi xóa đầu sách (BookDAO.deleteBook) do FK constraint.
     */
    public boolean deleteBookItem(String isbn) {
        String sql = "DELETE FROM tblBookItem WHERE tblBookISBN = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
