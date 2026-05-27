package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.BookItem;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Student;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO truy vấn lịch sử mượn trả của một cuốn sách
 * (spec §1.a: BorrowedBookDAO - getBorrowHistoryByBook()).
 */
public class BorrowedBookDAO extends DAO {

    /**
     * Lấy toàn bộ lịch sử mượn trả của một đầu sách theo bookId.
     * Spec §1.b bước 25-33: BorrowedBook -> Borrowing -> Student.
     */
    public List<BorrowedBook> getBorrowHistoryByBook(String bookId) {
        List<BorrowedBook> result = new ArrayList<>();
        String sql =
            "SELECT bb.id, bb.due_date, bb.return_date, bb.status, " +
            "       bi.barcode, bi.status AS item_status, " +
            "       br.borrow_id, br.borrow_date, " +
            "       s.student_code, s.full_name " +
            "FROM borrowed_books bb " +
            "JOIN book_items bi  ON bi.barcode    = bb.barcode " +
            "JOIN borrowings br  ON br.borrow_id  = bb.borrow_id " +
            "JOIN students s     ON s.student_code= br.student_code " +
            "WHERE bi.book_id = ? " +
            "ORDER BY br.borrow_date DESC";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, bookId);
            rs = ps.executeQuery();
            while (rs.next()) {
                // Đóng gói lồng nhau theo spec §1.b bước 27-31
                BookItem item = new BookItem(
                    rs.getString("barcode"),
                    rs.getString("item_status")
                );
                Student student = new Student(
                    rs.getString("student_code"),
                    rs.getString("full_name")
                );
                Borrowing borrowing = new Borrowing(
                    rs.getInt("borrow_id"),
                    student,
                    rs.getDate("borrow_date").toLocalDate()
                );
                Date returnDate = rs.getDate("return_date");
                BorrowedBook bb = new BorrowedBook(
                    borrowing,
                    item,
                    rs.getDate("due_date").toLocalDate(),
                    returnDate == null ? null : returnDate.toLocalDate(),
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
