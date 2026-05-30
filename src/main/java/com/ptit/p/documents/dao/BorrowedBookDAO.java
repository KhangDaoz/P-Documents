package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BookItem;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Student;
import com.ptit.p.documents.model.BorrowedBookFine;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO quản lý dữ liệu mượn trả của sách (tblBorrowedBook).
 * Kết hợp chức năng mượn trả của Huy/Hieu và báo cáo thống kê của Sang.
 */
public class BorrowedBookDAO extends DAO {

    public BorrowedBookDAO() {
        super();
    }

    /** Lấy chi tiết sách mượn theo Borrowing ID (phục vụ trả sách) */
    public List<BorrowedBook> findByBorrowingId(int borrowingId) {
        List<BorrowedBook> results = new ArrayList<>();
        String sql = "SELECT bb.ID, bb.expectedReturnDate, bb.actualReturnDate, bb.status, bb.note, bb.price, "
                   + "bi.ID AS bookItemId, bi.status AS bookItemStatus, bi.tblBookISBN AS bookISBN "
                   + "FROM tblBorrowedBook bb "
                   + "JOIN tblBookItem bi ON bb.tblBookItemID = bi.ID "
                   + "WHERE bb.tblBorrowingID = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, borrowingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    BookItem bookItem = new BookItem();
                    bookItem.setId(resultSet.getInt("bookItemId"));
                    bookItem.setStatus(resultSet.getString("bookItemStatus"));
                    bookItem.setBookISBN(resultSet.getString("bookISBN"));

                    BorrowedBook borrowedBook = new BorrowedBook(
                        resultSet.getInt("ID"),
                        resultSet.getTimestamp("expectedReturnDate") != null ? new java.util.Date(resultSet.getTimestamp("expectedReturnDate").getTime()) : null,
                        resultSet.getTimestamp("actualReturnDate") != null ? new java.util.Date(resultSet.getTimestamp("actualReturnDate").getTime()) : null,
                        resultSet.getString("status"),
                        resultSet.getString("note"),
                        resultSet.getDouble("price"),
                        bookItem,
                        new ArrayList<>()
                    );
                    results.add(borrowedBook);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return results;
    }

    /** Cập nhật trạng thái trả của từng cuốn sách */
    public boolean updateReturnStatus(BorrowedBook bb) {
        String sql = "UPDATE tblBorrowedBook SET actualReturnDate = ?, status = ?, note = ?, price = ? WHERE ID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, bb.getActualReturnDate() != null ? new java.sql.Date(bb.getActualReturnDate().getTime()) : null);
            statement.setString(2, bb.getStatus() != null ? bb.getStatus() : "good");
            statement.setString(3, bb.getNote());
            statement.setObject(4, bb.getPrice() != 0.0 ? bb.getPrice() : null);
            statement.setInt(5, bb.getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** Thiết lập phạt áp dụng cho lượt mượn sách hỏng/mất */
    public boolean setBorrowedBookFine(BorrowedBookFine fine, BorrowedBook bb) {
        String sql = "INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, fine.getFineRate());
            statement.setInt(2, bb.getId());
            statement.setInt(3, fine.getFine().getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy toàn bộ lịch sử mượn trả của một đầu sách theo ISBN (phục vụ Thống kê báo cáo).
     * Kết quả chứa: BorrowedBook -> Borrowing -> Student.
     */
    public List<BorrowedBook> getBorrowHistoryByBook(String isbn) {
        List<BorrowedBook> result = new ArrayList<>();
        String sql =
            "SELECT bb.ID, bb.expectedReturnDate, bb.actualReturnDate, bb.status, " +
            "       bi.ID AS itemId, bi.status AS item_status, " +
            "       br.ID AS borrowId, br.createdAt AS borrowDate, " +
            "       s.ID AS studentId, s.fullName " +
            "FROM tblBorrowedBook bb " +
            "JOIN tblBookItem bi  ON bi.ID       = bb.tblBookItemID " +
            "JOIN tblBorrowing br ON br.ID       = bb.tblBorrowingID " +
            "JOIN tblStudent s    ON s.ID        = br.tblStudentID " +
            "WHERE bi.tblBookISBN = ? " +
            "ORDER BY br.createdAt DESC";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookItem item = new BookItem(
                        rs.getInt("itemId"),
                        rs.getString("item_status")
                    );
                    Student student = new Student(
                        rs.getString("studentId"),
                        rs.getString("fullName")
                    );
                    Borrowing borrowing = new Borrowing();
                    borrowing.setId(rs.getInt("borrowId"));
                    borrowing.setStudent(student);
                    borrowing.setCreatedAt(rs.getTimestamp("borrowDate"));

                    BorrowedBook bb = new BorrowedBook();
                    bb.setId(rs.getInt("ID"));
                    bb.setBorrowing(borrowing);
                    bb.setBookItem(item);
                    bb.setExpectedReturnDate(rs.getTimestamp("expectedReturnDate"));
                    bb.setActualReturnDate(rs.getTimestamp("actualReturnDate"));
                    bb.setStatus(rs.getString("status"));

                    result.add(bb);
                }
            }
        } catch (Exception e) {
            System.err.println("[BorrowedBookDAO] getBorrowHistoryByBook lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
