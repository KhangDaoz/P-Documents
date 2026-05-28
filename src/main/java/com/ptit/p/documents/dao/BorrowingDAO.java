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

/**
 * DAO xử lý các thao tác truy xuất CSDL liên quan đến phiếu mượn (Borrowing).
 * Kế thừa lớp DAO để sử dụng kết nối CSDL dùng chung.
 *
 * Các phương thức:
 *   - addBorrowing()    : thêm phiếu mượn mới (module Đặt Sách)
 *   - searchBorrowing() : tìm phiếu đang "pending" theo thông tin SV (module Hủy)
 *   - cancelBorrowing() : hủy phiếu mượn (module Hủy)
 *
 * Thay đổi theo schema p_documents:
 *   - Không còn cột borrowDate — dùng createdAt (DEFAULT CURRENT_TIMESTAMP)
 *   - Không còn cột availableCopies trong tblBook — tính động qua subquery
 *   - BookItem.status: 'good'/'damaged'/'lost' (không còn 'available'/'reserved')
 *   - Borrowing.status: 'pending'/'borrowed'/'returned'/'overdue'/'cancelled'
 *   - BorrowedBook.status: 'good'/'damaged'/'lost'
 *   - Tìm bản sao khả dụng: status='good' AND không trong phiếu pending/borrowed
 */
public class BorrowingDAO extends DAO {

    /** Định dạng ngày dùng khi ghi DATE vào CSDL. */
    private static final SimpleDateFormat SDF_DATE =
            new SimpleDateFormat("yyyy-MM-dd");

    public BorrowingDAO() {
        super();
    }

    // =========================================================================
    //  MODULE ĐẶT SÁCH — addBorrowing()
    // =========================================================================

    /**
     * Thêm phiếu mượn mới vào CSDL trong một transaction.
     *
     * Luồng xử lý:
     *   1. INSERT vào tblBorrowing (status = 'pending', borrowDate tự động qua createdAt).
     *   2. Với mỗi BorrowedBook trong b.getBooks():
     *      a. Nếu bookItem chưa được gán → tự tìm bản sao 'good' không trong phiếu active.
     *      b. Nếu không còn bản sao → ROLLBACK, trả false.
     *      c. INSERT vào tblBorrowedBook.
     *   3. COMMIT nếu tất cả thành công.
     *
     * Lưu ý cho tầng View:
     *   - Mỗi BorrowedBook trong b.getBooks() phải có trường book (Book) đã set ISBN.
     *   - bookItem có thể null — DAO sẽ tự tìm bản sao khả dụng.
     *
     * @param b Đối tượng Borrowing đã đầy đủ thông tin (student, user, books)
     * @return true nếu thêm thành công, false nếu có lỗi hoặc không còn sách khả dụng
     */
    public boolean addBorrowing(Borrowing b) {
        // borrowDate không INSERT vì DB dùng createdAt DEFAULT CURRENT_TIMESTAMP
        String sqlAddBorrowing =
                "INSERT INTO tblBorrowing(expectedReceiveDate, note, status, tblStudentID, tblUserID)"
                + " VALUES(?,?,?,?,?)";

        // Bản sao khả dụng: status='good' VÀ không đang trong phiếu pending/borrowed
        String sqlFindBookItem =
                "SELECT ID FROM tblBookItem"
                + " WHERE tblBookISBN = ? AND status = 'good'"
                + " AND ID NOT IN ("
                + "   SELECT bb.tblBookItemID FROM tblBorrowedBook bb"
                + "   JOIN tblBorrowing br ON bb.tblBorrowingID = br.ID"
                + "   WHERE br.status IN ('pending','borrowed')"
                + " ) LIMIT 1";

        String sqlAddBorrowedBook =
                "INSERT INTO tblBorrowedBook(expectedReturnDate, status, note, price, tblBookItemID, tblBorrowingID)"
                + " VALUES(?,?,?,?,?,?)";

        boolean result = true;
        try {
            con.setAutoCommit(false);

            // ------- Bước 1: Thêm phiếu mượn -------
            PreparedStatement ps = con.prepareStatement(
                    sqlAddBorrowing, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, b.getExpectedReceiveDate() != null
                    ? SDF_DATE.format(b.getExpectedReceiveDate()) : null);
            ps.setString(2, b.getNote());
            ps.setString(3, b.getStatus() != null ? b.getStatus() : "pending");
            ps.setString(4, b.getStudent().getStudentId());
            ps.setInt(5, b.getUser().getId());
            ps.executeUpdate();

            // Lấy ID phiếu mượn vừa tạo
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                b.setId(generatedKeys.getInt(1));
            } else {
                con.rollback();
                con.setAutoCommit(true);
                return false;
            }

            // ------- Bước 2: Thêm từng BorrowedBook -------
            for (BorrowedBook bb : b.getBooks()) {
                String isbn = (bb.getBook() != null) ? bb.getBook().getIsbn() : null;
                int bookItemId = -1;

                // Xác định bookItemId
                if (bb.getBookItem() != null && bb.getBookItem().getId() > 0) {
                    // bookItem đã được chỉ định sẵn từ tầng View
                    bookItemId = bb.getBookItem().getId();
                } else if (isbn != null) {
                    // Tự tìm bản sao 'good' còn khả dụng cho ISBN tương ứng
                    ps = con.prepareStatement(sqlFindBookItem);
                    ps.setString(1, isbn);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        bookItemId = rs.getInt("ID");
                    } else {
                        // Không còn bản sao khả dụng → rollback
                        System.err.println("[BorrowingDAO] Không còn bản sao 'good' khả dụng cho ISBN: " + isbn);
                        con.rollback();
                        con.setAutoCommit(true);
                        return false;
                    }
                } else {
                    // Không có đủ thông tin → rollback
                    con.rollback();
                    con.setAutoCommit(true);
                    return false;
                }

                // INSERT vào tblBorrowedBook
                ps = con.prepareStatement(sqlAddBorrowedBook, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, bb.getExpectedReturnDate() != null
                        ? SDF_DATE.format(bb.getExpectedReturnDate()) : null);
                ps.setString(2, bb.getStatus() != null ? bb.getStatus() : "good");
                ps.setString(3, bb.getNote());
                ps.setBigDecimal(4, bb.getPrice());
                ps.setInt(5, bookItemId);
                ps.setInt(6, b.getId());
                ps.executeUpdate();

                // Gán lại ID cho BorrowedBook
                ResultSet bbKeys = ps.getGeneratedKeys();
                if (bbKeys.next()) {
                    bb.setId(bbKeys.getInt(1));
                }

                // Gán BookItem vào object BorrowedBook nếu chưa có
                if (bb.getBookItem() == null) {
                    BookItem bi = new BookItem();
                    bi.setId(bookItemId);
                    bb.setBookItem(bi);
                }
                // Lưu ý: Không UPDATE tblBookItem.status hay tblBook.availableCopies
                // vì status BookItem phản ánh tình trạng vật lý, không phải availability.
                // Availability được tính động qua subquery trong BookDAO.
            }

            // ------- Bước 3: Commit -------
            con.commit();
            con.setAutoCommit(true);

        } catch (Exception e) {
            result = false;
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return result;
    }

    // =========================================================================
    //  MODULE HỦY ĐẶT SÁCH — searchBorrowing() + cancelBorrowing()
    // =========================================================================

    /**
     * Tìm kiếm các phiếu mượn đang ở trạng thái 'pending'
     * theo mã sinh viên hoặc họ tên sinh viên (LIKE).
     *
     * SQL tương ứng:
     *   SELECT br.*, st.fullName, st.email, st.phone, st.address
     *   FROM tblBorrowing br
     *   JOIN tblStudent st ON br.tblStudentID = st.ID
     *   WHERE br.status = 'pending' AND (st.ID LIKE ? OR st.fullName LIKE ?)
     *
     * Sau đó tải thêm danh sách sách (BorrowedBook) cho từng phiếu qua loadBorrowedBooks().
     *
     * @param key Từ khóa tìm kiếm (mã SV hoặc một phần họ tên)
     * @return Danh sách phiếu mượn đang ở trạng thái 'pending'
     */
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
                b.setCreatedAt(rs.getDate("createdAt"));          // ngày tạo phiếu
                b.setExpectedReceiveDate(rs.getDate("expectedReceiveDate"));
                b.setActualReceiveDate(rs.getDate("actualReceiveDate"));
                b.setNote(rs.getString("note"));
                b.setStatus(rs.getString("status"));

                // Đóng gói thông tin sinh viên
                Student st = new Student();
                st.setStudentId(rs.getString("tblStudentID"));
                st.setFullName(rs.getString("fullName"));
                st.setEmail(rs.getString("email"));
                st.setPhone(rs.getString("phone"));
                st.setAddress(rs.getString("address"));
                b.setStudent(st);

                // Tải danh sách sách trong phiếu mượn
                b.setBooks(loadBorrowedBooks(b.getId()));

                result.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Tải danh sách BorrowedBook của một phiếu mượn theo borrowingId.
     * Join qua tblBookItem → tblBook để lấy thông tin đầu sách.
     *
     * @param borrowingId ID của phiếu mượn cần tải sách
     * @return Danh sách BorrowedBook
     */
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

                // Đóng gói BookItem
                BookItem bi = new BookItem();
                bi.setId(rs.getInt("tblBookItemID"));
                bi.setTblBookISBN(rs.getString("tblBookISBN"));
                bb.setBookItem(bi);

                // Đóng gói Book (đầu sách)
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

    /**
     * Hủy một phiếu mượn đang ở trạng thái 'pending'.
     * Toàn bộ thao tác thực hiện trong một transaction.
     *
     * Luồng xử lý:
     *   1. Kiểm tra status phiếu mượn — phải là 'pending'.
     *   2. UPDATE tblBorrowing SET status = 'cancelled' (với điều kiện status = 'pending').
     *   3. Nếu không UPDATE được → phiếu đã đổi trạng thái → ROLLBACK, trả false.
     *   4. UPDATE tblBorrowedBook SET status = 'good' (giữ nguyên status vật lý của sách).
     *   5. COMMIT.
     *
     * Lưu ý: Không UPDATE tblBookItem.status hay tblBook.availableCopies
     * vì BookItem.status phản ánh tình trạng vật lý, availability tính động.
     *
     * @param borrowingId ID của phiếu mượn cần hủy
     * @return true nếu hủy thành công, false nếu phiếu không hợp lệ hoặc xảy ra lỗi
     */
    public boolean cancelBorrowing(int borrowingId) {
        String sqlCheckStatus =
                "SELECT status FROM tblBorrowing WHERE ID = ?";
        String sqlCancelBorrowing =
                "UPDATE tblBorrowing SET status = 'cancelled'"
                + " WHERE ID = ? AND status = 'pending'";
        String sqlCancelBorrowedBooks =
                "UPDATE tblBorrowedBook SET status = 'good' WHERE tblBorrowingID = ?";

        boolean result = true;
        try {
            con.setAutoCommit(false);

            // ------- Bước 1: Kiểm tra trạng thái hiện tại -------
            PreparedStatement ps = con.prepareStatement(sqlCheckStatus);
            ps.setInt(1, borrowingId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next() || !"pending".equals(rs.getString("status"))) {
                System.err.println("[BorrowingDAO] Phiếu " + borrowingId
                        + " không ở trạng thái 'pending' — không thể hủy.");
                con.setAutoCommit(true);
                return false;
            }

            // ------- Bước 2: Cập nhật trạng thái phiếu mượn -------
            ps = con.prepareStatement(sqlCancelBorrowing);
            ps.setInt(1, borrowingId);
            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                // Trạng thái đã thay đổi giữa bước check và bước update (race condition)
                con.rollback();
                con.setAutoCommit(true);
                return false;
            }

            // ------- Bước 3: Cập nhật trạng thái tblBorrowedBook -------
            ps = con.prepareStatement(sqlCancelBorrowedBooks);
            ps.setInt(1, borrowingId);
            ps.executeUpdate();

            // ------- Bước 4: Commit -------
            con.commit();
            con.setAutoCommit(true);

        } catch (Exception e) {
            result = false;
            try {
                con.rollback();
                con.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return result;
    }
}
