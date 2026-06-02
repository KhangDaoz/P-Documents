package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BookItem;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Student;
import com.ptit.p.documents.model.BorrowedBookFine;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BorrowedBookDAO extends DAO {

    public BorrowedBookDAO() {
        super();
    }

    
    public List<BorrowedBook> findByBorrowingId(int borrowingId) {
        List<BorrowedBook> results = new ArrayList<>();
        String sql = "SELECT bb.ID, bb.expectedReturnDate, bb.actualReturnDate, bb.status, bb.note, bb.price, "
                + "bi.ID AS bookItemId, bi.status AS bookItemStatus, bi.tblBookISBN AS bookISBN "
                + "FROM tblBorrowedBook bb "
                + "JOIN tblBookItem bi ON bb.tblBookItemID = bi.ID "
                + "WHERE bb.tblBorrowingID = ?";

        try (PreparedStatement statement = getCon().prepareStatement(sql)) {
            statement.setInt(1, borrowingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    BookItem bookItem = new BookItem();
                    bookItem.setId(resultSet.getInt("bookItemId"));
                    bookItem.setStatus(resultSet.getString("bookItemStatus"));
                    bookItem.setBookISBN(resultSet.getString("bookISBN"));

                    BorrowedBook borrowedBook = new BorrowedBook();
                    borrowedBook.setId(resultSet.getInt("ID"));
                    borrowedBook.setExpectedReturnDate(resultSet.getTimestamp("expectedReturnDate") != null
                                    ? resultSet.getTimestamp("expectedReturnDate").toLocalDateTime().toLocalDate()
                                    : null);
                    borrowedBook.setActualReturnDate(resultSet.getTimestamp("actualReturnDate") != null
                                    ? resultSet.getTimestamp("actualReturnDate").toLocalDateTime().toLocalDate()
                                    : null);
                    borrowedBook.setStatus(resultSet.getString("status"));
                    borrowedBook.setNote(resultSet.getString("note"));
                    borrowedBook.setPrice(resultSet.getDouble("price"));
                    borrowedBook.setBookItem(bookItem);
                    borrowedBook.setBorrowedBookFines(new ArrayList<>());
                    results.add(borrowedBook);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return results;
    }

    
    public boolean updateReturnStatus(BorrowedBook bb) {
        String sql = "UPDATE tblBorrowedBook SET actualReturnDate = ?, status = ?, note = ?, price = ? WHERE ID = ?";
        try (PreparedStatement statement = getCon().prepareStatement(sql)) {
            statement.setDate(1,
                    bb.getActualReturnDate() != null ? java.sql.Date.valueOf(bb.getActualReturnDate()) : null);
            statement.setString(2, bb.getStatus() != null ? bb.getStatus() : "good");
            statement.setString(3, bb.getNote());
            statement.setDouble(4, bb.getPrice());
            statement.setInt(5, bb.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    
    public boolean setBorrowedBookFine(BorrowedBookFine fine, BorrowedBook bb) {
        String sql = "INSERT INTO tblBorrowedBookFine (fineRate, tblBorrowedBookID, tblFineID) VALUES (?, ?, ?)";
        try (PreparedStatement statement = getCon().prepareStatement(sql)) {
            statement.setDouble(1, fine.getFineRate());
            statement.setInt(2, bb.getId());
            statement.setInt(3, fine.getFine().getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    
    public List<BorrowedBook> getBorrowHistoryByBook(String isbn) {
        List<BorrowedBook> result = new ArrayList<>();
        String sql = "SELECT bb.ID, bb.expectedReturnDate, bb.actualReturnDate, bb.status, " +
                "       bi.ID AS itemId, bi.status AS item_status, " +
                "       br.ID AS borrowId, br.createdAt AS borrowDate, " +
                "       s.ID AS studentId, s.fullName " +
                "FROM tblBorrowedBook bb " +
                "JOIN tblBookItem bi  ON bi.ID       = bb.tblBookItemID " +
                "JOIN tblBorrowing br ON br.ID       = bb.tblBorrowingID " +
                "JOIN tblStudent s    ON s.ID        = br.tblStudentID " +
                "WHERE bi.tblBookISBN = ? " +
                "ORDER BY br.createdAt DESC";

        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookItem item = new BookItem(
                            rs.getInt("itemId"),
                            rs.getString("item_status"));
                    Student student = new Student(
                            rs.getString("studentId"),
                            rs.getString("fullName"));
                    Borrowing borrowing = new Borrowing();
                    borrowing.setId(rs.getInt("borrowId"));
                    borrowing.setStudent(student);
                    borrowing.setCreatedAt(rs.getTimestamp("borrowDate") != null
                            ? rs.getTimestamp("borrowDate").toLocalDateTime().toLocalDate()
                            : null);

                    BorrowedBook bb = new BorrowedBook();
                    bb.setId(rs.getInt("ID"));
                    bb.setBorrowing(borrowing);
                    bb.setBookItem(item);
                    bb.setExpectedReturnDate(rs.getTimestamp("expectedReturnDate") != null
                            ? rs.getTimestamp("expectedReturnDate").toLocalDateTime().toLocalDate()
                            : null);
                    bb.setActualReturnDate(rs.getTimestamp("actualReturnDate") != null
                            ? rs.getTimestamp("actualReturnDate").toLocalDateTime().toLocalDate()
                            : null);
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
