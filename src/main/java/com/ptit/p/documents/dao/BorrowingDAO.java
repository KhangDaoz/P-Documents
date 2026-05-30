package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BookItem;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Student;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BorrowingDAO extends DAO {

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");

    public BorrowingDAO() {
        super();
    }

    // Tìm bản sao status='good' chưa nằm trong phiếu pending/borrowed
    private static final String SQL_FIND_BOOK_ITEM =
            "SELECT ID FROM tblBookItem"
            + " WHERE tblBookISBN = ? AND status = 'good'"
            + " AND ID NOT IN ("
            + "   SELECT bb.tblBookItemID FROM tblBorrowedBook bb"
            + "   JOIN tblBorrowing br ON bb.tblBorrowingID = br.ID"
            + "   WHERE br.status IN ('pending','borrowed')"
            + " ) LIMIT 1";

    public boolean addBorrowing(Borrowing b) {
        String sqlAddBorrowing =
                "INSERT INTO tblBorrowing(expectedReceiveDate, note, status, tblStudentID, tblUserID)"
                + " VALUES(?,?,?,?,?)";
        String sqlAddBorrowedBook =
                "INSERT INTO tblBorrowedBook(expectedReturnDate, status, note, price, tblBookItemID, tblBorrowingID)"
                + " VALUES(?,?,?,?,?,?)";

        boolean result = true;
        try {
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sqlAddBorrowing, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, b.getExpectedReceiveDate() != null ? SDF_DATE.format(b.getExpectedReceiveDate()) : null);
            ps.setString(2, b.getNote());
            ps.setString(3, b.getStatus() != null ? b.getStatus() : "pending");
            ps.setString(4, b.getStudent().getStudentId());
            ps.setInt(5, b.getUser().getId());
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                b.setId(generatedKeys.getInt(1));
            } else {
                con.rollback(); con.setAutoCommit(true); return false;
            }

            for (BorrowedBook bb : b.getBooks()) {
                String isbn = (bb.getBook() != null) ? bb.getBook().getIsbn() : null;
                int bookItemId = -1;

                if (bb.getBookItem() != null && bb.getBookItem().getId() > 0) {
                    bookItemId = bb.getBookItem().getId();
                } else if (isbn != null) {
                    ps = con.prepareStatement(SQL_FIND_BOOK_ITEM);
                    ps.setString(1, isbn);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        bookItemId = rs.getInt("ID");
                    } else {
                        con.rollback(); con.setAutoCommit(true); return false;
                    }
                } else {
                    con.rollback(); con.setAutoCommit(true); return false;
                }

                ps = con.prepareStatement(sqlAddBorrowedBook, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, bb.getExpectedReturnDate() != null ? SDF_DATE.format(bb.getExpectedReturnDate()) : null);
                ps.setString(2, bb.getStatus() != null ? bb.getStatus() : "good");
                ps.setString(3, bb.getNote());
                ps.setBigDecimal(4, bb.getPrice());
                ps.setInt(5, bookItemId);
                ps.setInt(6, b.getId());
                ps.executeUpdate();

                ResultSet bbKeys = ps.getGeneratedKeys();
                if (bbKeys.next()) bb.setId(bbKeys.getInt(1));

                if (bb.getBookItem() == null) {
                    BookItem bi = new BookItem();
                    bi.setId(bookItemId);
                    bb.setBookItem(bi);
                }
            }

            con.commit();
            con.setAutoCommit(true);

        } catch (Exception e) {
            result = false;
            try { con.rollback(); con.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Borrowing> searchBorrowing(String key) {
        ArrayList<Borrowing> result = new ArrayList<>();
        String sql =
                "SELECT br.ID, br.expectedReceiveDate, br.actualReceiveDate,"
                + " br.note, br.status, br.tblStudentID, br.createdAt,"
                + " st.fullName, st.email, st.phone, st.address"
                + " FROM tblBorrowing br"
                + " JOIN tblStudent st ON br.tblStudentID = st.ID"
                + " WHERE br.status = 'pending'"
                + " AND (st.ID LIKE ? OR st.fullName LIKE ?)"
                + " ORDER BY br.createdAt DESC";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            String pattern = "%" + key + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Borrowing b = new Borrowing();
                b.setId(rs.getInt("ID"));
                b.setCreatedAt(rs.getDate("createdAt"));
                b.setExpectedReceiveDate(rs.getDate("expectedReceiveDate"));
                b.setActualReceiveDate(rs.getDate("actualReceiveDate"));
                b.setNote(rs.getString("note"));
                b.setStatus(rs.getString("status"));

                Student st = new Student();
                st.setStudentId(rs.getString("tblStudentID"));
                st.setFullName(rs.getString("fullName"));
                st.setEmail(rs.getString("email"));
                st.setPhone(rs.getString("phone"));
                st.setAddress(rs.getString("address"));
                b.setStudent(st);

                b.setBooks(loadBorrowedBooks(b.getId()));
                result.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<BorrowedBook> loadBorrowedBooks(int borrowingId) {
        ArrayList<BorrowedBook> list = new ArrayList<>();
        String sql =
                "SELECT bb.ID, bb.expectedReturnDate, bb.actualReturnDate, bb.status,"
                + " bb.note, bb.price, bb.tblBookItemID,"
                + " bi.tblBookISBN, bk.ISBN, bk.title, bk.author, bk.genre"
                + " FROM tblBorrowedBook bb"
                + " JOIN tblBookItem bi ON bb.tblBookItemID = bi.ID"
                + " JOIN tblBook bk ON bi.tblBookISBN = bk.ISBN"
                + " WHERE bb.tblBorrowingID = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, borrowingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BorrowedBook bb = new BorrowedBook();
                bb.setId(rs.getInt("ID"));
                bb.setExpectedReturnDate(rs.getDate("expectedReturnDate"));
                bb.setActualReturnDate(rs.getDate("actualReturnDate"));
                bb.setStatus(rs.getString("status"));
                bb.setNote(rs.getString("note"));
                bb.setPrice(rs.getBigDecimal("price"));

                BookItem bi = new BookItem();
                bi.setId(rs.getInt("tblBookItemID"));
                bi.setTblBookISBN(rs.getString("tblBookISBN"));
                bb.setBookItem(bi);

                Book book = new Book();
                book.setIsbn(rs.getString("ISBN"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setGenre(rs.getString("genre"));
                bb.setBook(book);

                list.add(bb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean cancelBorrowing(int borrowingId) {
        String sqlCheck  = "SELECT status FROM tblBorrowing WHERE ID = ?";
        String sqlCancel = "UPDATE tblBorrowing SET status = 'cancelled' WHERE ID = ? AND status = 'pending'";
        String sqlCancelBooks = "UPDATE tblBorrowedBook SET status = 'good' WHERE tblBorrowingID = ?";

        boolean result = true;
        try {
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sqlCheck);
            ps.setInt(1, borrowingId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next() || !"pending".equals(rs.getString("status"))) {
                con.setAutoCommit(true);
                return false;
            }

            ps = con.prepareStatement(sqlCancel);
            ps.setInt(1, borrowingId);
            if (ps.executeUpdate() == 0) {
                con.rollback(); con.setAutoCommit(true); return false;
            }

            ps = con.prepareStatement(sqlCancelBooks);
            ps.setInt(1, borrowingId);
            ps.executeUpdate();

            con.commit();
            con.setAutoCommit(true);

        } catch (Exception e) {
            result = false;
            try { con.rollback(); con.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
            e.printStackTrace();
        }
        return result;
    }
}
