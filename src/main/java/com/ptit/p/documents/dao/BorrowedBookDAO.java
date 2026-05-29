package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BookItem;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Student;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO truy vấn lịch sử mượn trả của một cuốn sách.
 * Truy vấn trên schema p_documents: tblBorrowedBook, tblBookItem, tblBorrowing, tblStudent.
 */
public class BorrowedBookDAO extends DAO {

    /**
     * Lấy toàn bộ lịch sử mượn trả của một đầu sách theo ISBN.
     * BorrowedBook -> Borrowing -> Student.
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

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, isbn);
            rs = ps.executeQuery();
            while (rs.next()) {
                BookItem item = new BookItem(
                    rs.getInt("itemId"),
                    rs.getString("item_status")
                );
                Student student = new Student(
                    rs.getInt("studentId"),
                    rs.getString("fullName")
                );
                // createdAt (TIMESTAMP) → LocalDate
                Timestamp borrowTs = rs.getTimestamp("borrowDate");
                Borrowing borrowing = new Borrowing(
                    rs.getInt("borrowId"),
                    student,
                    borrowTs == null ? null : borrowTs.toLocalDateTime().toLocalDate()
                );
                Date actualReturnDate = rs.getDate("actualReturnDate");
                BorrowedBook bb = new BorrowedBook(
                    borrowing,
                    item,
                    rs.getDate("expectedReturnDate").toLocalDate(),
                    actualReturnDate == null ? null : actualReturnDate.toLocalDate(),
                    rs.getString("status")
                );
                result.add(bb);
            }
        } catch (Exception e) {
            System.err.println("[BorrowedBookDAO] getBorrowHistoryByBook lỗi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            close(ps, rs);
        }
        return result;
    }
}
