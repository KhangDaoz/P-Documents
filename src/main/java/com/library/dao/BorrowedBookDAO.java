package com.library.dao;

import com.library.model.BookItem;
import com.library.model.Book;
import com.library.model.BorrowedBook;
import com.library.model.Borrowing;
import com.library.model.Student;

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
     * Spec §1.b bước 25-33.
     */
    public List<BorrowedBook> getBorrowHistoryByBook(String bookId) {
        List<BorrowedBook> result = new ArrayList<>();
        String sql =
            "SELECT bb.id, bb.due_date, bb.return_date, bb.status, " +
            "       bi.barcode, bi.status AS item_status, " +
            "       b.book_id, b.title, b.author, b.category, " +
            "       br.borrow_id, br.borrow_date, " +
            "       s.student_code, s.full_name " +
            "FROM borrowed_books bb " +
            "JOIN book_items bi  ON bi.barcode    = bb.barcode " +
            "JOIN books b        ON b.book_id     = bi.book_id " +
            "JOIN borrowings br  ON br.borrow_id  = bb.borrow_id " +
            "JOIN students s     ON s.student_code= br.student_code " +
            "WHERE b.book_id = ? " +
            "ORDER BY br.borrow_date DESC";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, bookId);
            rs = ps.executeQuery();
            while (rs.next()) {
                // Đóng gói lồng nhau theo spec §1.b bước 27-31
                Book book = new Book(
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category")
                );
                BookItem item = new BookItem(
                    rs.getString("barcode"),
                    rs.getString("item_status"),
                    book
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
