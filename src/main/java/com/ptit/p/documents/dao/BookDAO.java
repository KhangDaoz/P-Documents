package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO extends DAO {

    /**
     * Thêm sách mới vào CSDL.
     * Kiểm tra ISBN trùng trước khi thêm.
     * @return true nếu thêm thành công, false nếu ISBN đã tồn tại hoặc lỗi
     */
    public boolean addBook(Book book) {
        // Kiểm tra ISBN đã tồn tại
        String checkSql = "SELECT 1 FROM tblBook WHERE ISBN = ?";
        Connection conn = getConnection();
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, book.getISBN());
            try (ResultSet crs = checkPs.executeQuery()) {
                if (crs.next()) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO tblBook (ISBN, title, author, genre, publisher, publishYear, price, description) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getISBN());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getGenre());
            ps.setString(5, book.getPublisher());
            ps.setInt(6, book.getPublishYear());
            ps.setDouble(7, book.getPrice());
            ps.setString(8, book.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tìm sách theo từ khóa (tên sách, tác giả hoặc ISBN).
     * Sử dụng LIKE để tìm kiếm gần đúng.
     */
    public List<Book> searchBook(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, "
                   + "COALESCE(bi.totalCopies, 0) AS totalCopies, "
                   + "COALESCE(bi.availableCopies, 0) AS availableCopies "
                   + "FROM tblBook b "
                   + "LEFT JOIN ( "
                   + "    SELECT tblBookISBN, COUNT(*) AS totalCopies, "
                   + "           SUM(CASE WHEN status = 'good' THEN 1 ELSE 0 END) AS availableCopies "
                   + "    FROM tblBookItem "
                   + "    GROUP BY tblBookISBN "
                   + ") bi ON b.ISBN = bi.tblBookISBN "
                   + "WHERE b.title LIKE ? OR b.author LIKE ? OR b.ISBN LIKE ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setISBN(rs.getString("ISBN"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setGenre(rs.getString("genre"));
                    book.setPublisher(rs.getString("publisher"));
                    book.setPublishYear(rs.getInt("publishYear"));
                    book.setPrice(rs.getDouble("price"));
                    book.setDescription(rs.getString("description"));
                    book.setAvailableCopies(rs.getInt("availableCopies"));
                    book.setTotalCopies(rs.getInt("totalCopies"));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Cập nhật thông tin sách theo ISBN.
     * Không cho phép sửa ISBN và số lượng bản copy.
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE tblBook SET title = ?, author = ?, genre = ?, publisher = ?, "
               + "publishYear = ?, price = ?, description = ? WHERE ISBN = ?";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setString(4, book.getPublisher());
            ps.setInt(5, book.getPublishYear());
            ps.setDouble(6, book.getPrice());
            ps.setString(7, book.getDescription());
            ps.setString(8, book.getISBN());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa đầu sách theo ISBN.
     * Lưu ý: phải xóa BookItem trước khi gọi hàm này (do FK constraint).
     */
    public boolean deleteBook(String isbn) {
        Connection conn = getConnection();
        String deleteItemsSql = "DELETE FROM tblBookItem WHERE tblBookISBN = ?";
        String deleteBookSql = "DELETE FROM tblBook WHERE ISBN = ?";
        try (PreparedStatement itemPs = conn.prepareStatement(deleteItemsSql);
             PreparedStatement bookPs = conn.prepareStatement(deleteBookSql)) {
            itemPs.setString(1, isbn);
            itemPs.executeUpdate();

            bookPs.setString(1, isbn);
            return bookPs.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra tình trạng mượn của sách.
     * Nếu includeHistory = true, kiểm tra mọi lịch sử mượn để phục vụ xoá cứng.
     */
    public boolean checkBookStatus(String isbn, boolean includeHistory) {
        String sql = includeHistory
                ? "SELECT COUNT(*) AS cnt FROM tblBorrowedBook bb "
                + "JOIN tblBookItem bi ON bb.tblBookItemID = bi.ID "
                + "WHERE bi.tblBookISBN = ?"
                : "SELECT COUNT(*) AS cnt FROM tblBorrowing br "
                + "JOIN tblBorrowedBook bb ON br.ID = bb.tblBorrowingID "
                + "JOIN tblBookItem bi ON bb.tblBookItemID = bi.ID "
                + "WHERE bi.tblBookISBN = ? AND br.status IN ('pending', 'borrowed', 'overdue')";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
