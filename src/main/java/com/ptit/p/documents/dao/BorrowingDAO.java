package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BookItem;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Student;
import com.ptit.p.documents.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO extends DAO {

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");

    public BorrowingDAO() {
        super();
    }



    public boolean addBorrowing(Borrowing b) {
        if (b.getBooks() == null || b.getBooks().isEmpty()) {
            return false;
        }
        if (b.getExpectedReceiveDate() != null && b.getExpectedReceiveDate().isBefore(java.time.LocalDate.now())) {
            return false;
        }
        String sqlAddBorrowing = "INSERT INTO tblBorrowing(expectedReceiveDate, note, status, tblStudentID, tblUserID)"
                + " VALUES(?,?,?,?,?)";
        String sqlAddBorrowedBook = "INSERT INTO tblBorrowedBook(expectedReturnDate, status, note, price, tblBookItemID, tblBorrowingID)"
                + " VALUES(?,?,?,?,?,?)";

        boolean result = true;
        try {
            getCon().setAutoCommit(false);

            PreparedStatement ps = getCon().prepareStatement(sqlAddBorrowing, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1,
                    b.getExpectedReceiveDate() != null ? java.sql.Date.valueOf(b.getExpectedReceiveDate()) : null);
            ps.setString(2, b.getNote());
            ps.setString(3, b.getStatus() != null ? b.getStatus() : "pending");
            ps.setString(4, b.getStudent().getStudentId());
            ps.setInt(5, b.getUser().getId());
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                b.setId(generatedKeys.getInt(1));
            } else {
                getCon().rollback();
                getCon().setAutoCommit(true);
                return false;
            }

            for (BorrowedBook bb : b.getBooks()) {
                String isbn = (bb.getBook() != null) ? bb.getBook().getIsbn() : null;
                int bookItemId = -1;

                if (bb.getBookItem() != null && bb.getBookItem().getId() > 0) {
                    bookItemId = bb.getBookItem().getId();
                } else if (isbn != null) {
                    ps = getCon().prepareStatement("SELECT ID FROM tblBookItem"
                            + " WHERE tblBookISBN = ? AND status = 'good'"
                            + " AND ID NOT IN ("
                            + "   SELECT bb.tblBookItemID FROM tblBorrowedBook bb"
                            + "   JOIN tblBorrowing br ON bb.tblBorrowingID = br.ID"
                            + "   WHERE br.status IN ('pending','borrowed')"
                            + " ) LIMIT 1");
                    ps.setString(1, isbn);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        bookItemId = rs.getInt("ID");
                    } else {
                        getCon().rollback();
                        getCon().setAutoCommit(true);
                        return false;
                    }
                } else {
                    getCon().rollback();
                    getCon().setAutoCommit(true);
                    return false;
                }

                ps = getCon().prepareStatement(sqlAddBorrowedBook, Statement.RETURN_GENERATED_KEYS);
                ps.setDate(1,
                        bb.getExpectedReturnDate() != null ? java.sql.Date.valueOf(bb.getExpectedReturnDate()) : null);
                ps.setString(2, bb.getStatus() != null ? bb.getStatus() : "good");
                ps.setString(3, bb.getNote());
                ps.setDouble(4, bb.getPrice());
                ps.setInt(5, bookItemId);
                ps.setInt(6, b.getId());
                ps.executeUpdate();

                ResultSet bbKeys = ps.getGeneratedKeys();
                if (bbKeys.next())
                    bb.setId(bbKeys.getInt(1));

                if (bb.getBookItem() == null) {
                    BookItem bi = new BookItem();
                    bi.setId(bookItemId);
                    bb.setBookItem(bi);
                }
            }

            getCon().commit();
            getCon().setAutoCommit(true);

        } catch (Exception e) {
            result = false;
            try {
                getCon().rollback();
                getCon().setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return result;
    }

    public List<Borrowing> searchBorrowing(String studentId, String studentName, String... statuses) {
        List<Borrowing> results = new ArrayList<>();
        //find pending status Borrowing
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b.ID, b.expectedReceiveDate, b.actualReceiveDate, b.status, b.createdAt, ")
                .append("s.ID AS studentId, s.fullName AS studentName, s.email, s.phone AS studentPhone, s.address, ")
                .append("u.ID AS userId, u.username, u.password, u.fullName AS userFullName, u.phone AS userPhone, u.role ")
                .append("FROM tblBorrowing b ")
                .append("JOIN tblStudent s ON b.tblStudentID = s.ID ")
                .append("JOIN tblUser u ON b.tblUserID = u.ID ")
                .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (studentId != null && !studentId.isBlank()) {
            sql.append("AND s.ID = ? ");
            params.add(studentId);
        }
        if (studentName != null && !studentName.isBlank()) {
            sql.append("AND s.fullName LIKE ? ");
            params.add("%" + studentName + "%");
        }
        if (statuses != null && statuses.length > 0) {
            sql.append("AND b.status IN (");
            for (int i = 0; i < statuses.length; i++) {
                sql.append(i > 0 ? ", ?" : "?");
                params.add(statuses[i]);
            }
            sql.append(") ");
        }

        try (PreparedStatement statement = getCon().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Borrowing borrowing = new Borrowing();
                    borrowing.setId(resultSet.getInt("ID"));
                    borrowing.setExpectedReceiveDate(resultSet.getDate("expectedReceiveDate") != null
                            ? resultSet.getDate("expectedReceiveDate").toLocalDate()
                            : null);
                    borrowing.setActualReceiveDate(resultSet.getDate("actualReceiveDate") != null
                            ? resultSet.getDate("actualReceiveDate").toLocalDate()
                            : null);
                    borrowing.setStatus(resultSet.getString("status"));

                    Timestamp createdAt = resultSet.getTimestamp("createdAt");
                    if (createdAt != null) {
                        borrowing.setCreatedAt(createdAt.toLocalDateTime().toLocalDate());
                    }

                    Student student = new Student();
                    student.setStudentId(resultSet.getString("studentId"));
                    student.setFullName(resultSet.getString("studentName"));
                    student.setEmail(resultSet.getString("email"));
                    student.setPhone(resultSet.getString("studentPhone"));
                    student.setAddress(resultSet.getString("address"));
                    borrowing.setStudent(student);

                    User user = new User();
                    user.setId(resultSet.getInt("userId"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setFullName(resultSet.getString("userFullName"));
                    user.setPhone(resultSet.getString("userPhone"));
                    user.setRole(resultSet.getString("role"));
                    borrowing.setUser(user);

                    
                    ArrayList<BorrowedBook> books = new ArrayList<>();
                    String sqlBorrowedBooks = "SELECT bb.ID, bb.expectedReturnDate, bb.actualReturnDate, bb.status,"
                            + " bb.note, bb.price, bb.tblBookItemID,"
                            + " bi.tblBookISBN, bk.ISBN, bk.title, bk.author, bk.genre"
                            + " FROM tblBorrowedBook bb"
                            + " JOIN tblBookItem bi ON bb.tblBookItemID = bi.ID"
                            + " JOIN tblBook bk ON bi.tblBookISBN = bk.ISBN"
                            + " WHERE bb.tblBorrowingID = ?";
                    try (PreparedStatement psBooks = getCon().prepareStatement(sqlBorrowedBooks)) {
                        psBooks.setInt(1, borrowing.getId());
                        try (ResultSet rsBooks = psBooks.executeQuery()) {
                            while (rsBooks.next()) {
                                BorrowedBook bb = new BorrowedBook();
                                bb.setId(rsBooks.getInt("ID"));
                                bb.setExpectedReturnDate(
                                        rsBooks.getDate("expectedReturnDate") != null
                                                ? rsBooks.getDate("expectedReturnDate").toLocalDate()
                                                : null);
                                bb.setActualReturnDate(
                                        rsBooks.getDate("actualReturnDate") != null
                                                ? rsBooks.getDate("actualReturnDate").toLocalDate()
                                                : null);
                                bb.setStatus(rsBooks.getString("status"));
                                bb.setNote(rsBooks.getString("note"));
                                bb.setPrice(rsBooks.getDouble("price"));

                                BookItem bi = new BookItem();
                                bi.setId(rsBooks.getInt("tblBookItemID"));
                                bi.setBookISBN(rsBooks.getString("tblBookISBN"));
                                bb.setBookItem(bi);

                                Book book = new Book();
                                book.setIsbn(rsBooks.getString("ISBN"));
                                book.setTitle(rsBooks.getString("title"));
                                book.setAuthor(rsBooks.getString("author"));
                                book.setGenre(rsBooks.getString("genre"));
                                bb.setBook(book);

                                books.add(bb);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    borrowing.setBooks(books);

                    results.add(borrowing);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return results;
    }

    public ArrayList<Borrowing> searchBorrowing(String key) {
        ArrayList<Borrowing> result = new ArrayList<>();
        String sql = "SELECT br.ID, br.expectedReceiveDate, br.actualReceiveDate,"
                + " br.note, br.status, br.tblStudentID, br.createdAt,"
                + " st.fullName, st.email, st.phone, st.address"
                + " FROM tblBorrowing br"
                + " JOIN tblStudent st ON br.tblStudentID = st.ID"
                + " WHERE br.status = 'pending'"
                + " AND (st.ID LIKE ? OR st.fullName LIKE ?)"
                + " ORDER BY br.createdAt DESC";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            String pattern = "%" + key + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Borrowing b = new Borrowing();
                    b.setId(rs.getInt("ID"));
                    b.setCreatedAt(rs.getDate("createdAt") != null ? rs.getDate("createdAt").toLocalDate() : null);
                    b.setExpectedReceiveDate(
                            rs.getDate("expectedReceiveDate") != null ? rs.getDate("expectedReceiveDate").toLocalDate()
                                    : null);
                    b.setActualReceiveDate(
                            rs.getDate("actualReceiveDate") != null ? rs.getDate("actualReceiveDate").toLocalDate()
                                    : null);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public boolean cancelBorrowing(int borrowingId) {
        String sqlCheck = "SELECT status FROM tblBorrowing WHERE ID = ?";
        String sqlCancel = "UPDATE tblBorrowing SET status = 'cancelled' WHERE ID = ? AND status = 'pending'";
        String sqlCancelBooks = "UPDATE tblBorrowedBook SET status = 'good' WHERE tblBorrowingID = ?";

        boolean result = true;
        try {
            getCon().setAutoCommit(false);

            PreparedStatement ps = getCon().prepareStatement(sqlCheck);
            ps.setInt(1, borrowingId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next() || !"pending".equals(rs.getString("status"))) {
                getCon().setAutoCommit(true);
                return false;
            }

            ps = getCon().prepareStatement(sqlCancel);
            ps.setInt(1, borrowingId);
            if (ps.executeUpdate() == 0) {
                getCon().rollback();
                getCon().setAutoCommit(true);
                return false;
            }

            ps = getCon().prepareStatement(sqlCancelBooks);
            ps.setInt(1, borrowingId);
            ps.executeUpdate();

            getCon().commit();
            getCon().setAutoCommit(true);

        } catch (Exception e) {
            result = false;
            try {
                getCon().rollback();
                getCon().setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return result;
    }


    
    public boolean confirmBorrowing(int borrowingId, java.time.LocalDate actualReceiveDate) {
        String sql = "UPDATE tblBorrowing SET actualReceiveDate = ?, status = ? WHERE ID = ?";
        try (PreparedStatement statement = getCon().prepareStatement(sql)) {
            statement.setDate(1, actualReceiveDate != null ? java.sql.Date.valueOf(actualReceiveDate) : null);
            statement.setString(2, "borrowed");
            statement.setInt(3, borrowingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    
    public boolean updateBorrowing(int borrowingId, java.time.LocalDate actualReceiveDate, String status) {
        String sql = "UPDATE tblBorrowing SET actualReceiveDate = ?, status = ? WHERE ID = ?";
        try (PreparedStatement statement = getCon().prepareStatement(sql)) {
            statement.setDate(1, actualReceiveDate != null ? java.sql.Date.valueOf(actualReceiveDate) : null);
            statement.setString(2, status);
            statement.setInt(3, borrowingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
