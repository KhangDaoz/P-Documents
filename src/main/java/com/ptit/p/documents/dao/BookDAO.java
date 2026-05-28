package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * DAO xử lý các thao tác truy xuất CSDL liên quan đến đầu sách (Book).
 * Kế thừa lớp DAO để sử dụng kết nối CSDL dùng chung.
 *
 * Các phương thức:
 *   - searchBook()  : tìm sách theo từ khóa (tên, tác giả, thể loại, ISBN)
 *   - findAll()     : lấy toàn bộ danh sách sách
 *
 * Lưu ý về availableCopies:
 *   Đây là thuộc tính DẪN XUẤT — không có trong tblBook.
 *   Được tính động bằng cách đếm số BookItem có status='good' và KHÔNG đang
 *   nằm trong phiếu mượn active (status IN ('pending','borrowed')).
 */
public class BookDAO extends DAO {

    public BookDAO() {
        super();
    }

    /**
     * Subquery để đếm số bản sao khả dụng của một đầu sách.
     * Bản sao khả dụng = tblBookItem.status = 'good'
     *   AND ID NOT IN (đang nằm trong phiếu pending/borrowed).
     */
    private static final String AVAILABLE_COPIES_SUBQUERY =
            "(SELECT COUNT(*) FROM tblBookItem bi2"
            + " WHERE bi2.tblBookISBN = bk.ISBN"
            + " AND bi2.status = 'good'"
            + " AND bi2.ID NOT IN ("
            + "   SELECT bb2.tblBookItemID FROM tblBorrowedBook bb2"
            + "   JOIN tblBorrowing br2 ON bb2.tblBorrowingID = br2.ID"
            + "   WHERE br2.status IN ('pending','borrowed')"
            + " )) AS availableCopies";

    /**
     * Tìm kiếm sách theo các từ khóa (tất cả đều không bắt buộc — trống = khớp tất cả).
     * Trả về tất cả sách phù hợp, kể cả sách có availableCopies = 0.
     *
     * @param name   Từ khóa tên sách (có thể null hoặc rỗng)
     * @param author Từ khóa tác giả (có thể null hoặc rỗng)
     * @param genre  Từ khóa thể loại (có thể null hoặc rỗng)
     * @param isbn   Từ khóa ISBN (có thể null hoặc rỗng)
     * @return Danh sách các đầu sách phù hợp
     */
    public ArrayList<Book> searchBook(String name, String author,
                                      String genre, String isbn) {
        ArrayList<Book> result = new ArrayList<>();
        String sql = "SELECT bk.ISBN, bk.title, bk.author, bk.genre,"
                   + " bk.publisher, bk.publishYear, bk.price, bk.description, "
                   + AVAILABLE_COPIES_SUBQUERY
                   + " FROM tblBook bk"
                   + " WHERE bk.title LIKE ? AND bk.author LIKE ?"
                   + " AND bk.genre LIKE ? AND bk.ISBN LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + (name   != null ? name   : "") + "%");
            ps.setString(2, "%" + (author != null ? author : "") + "%");
            ps.setString(3, "%" + (genre  != null ? genre  : "") + "%");
            ps.setString(4, "%" + (isbn   != null ? isbn   : "") + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy toàn bộ danh sách đầu sách trong CSDL.
     *
     * @return Danh sách tất cả đầu sách
     */
    public ArrayList<Book> findAll() {
        ArrayList<Book> result = new ArrayList<>();
        String sql = "SELECT bk.ISBN, bk.title, bk.author, bk.genre,"
                   + " bk.publisher, bk.publishYear, bk.price, bk.description, "
                   + AVAILABLE_COPIES_SUBQUERY
                   + " FROM tblBook bk";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Ánh xạ một dòng ResultSet thành đối tượng Book. */
    private Book mapRow(ResultSet rs) throws Exception {
        Book book = new Book();
        book.setIsbn(rs.getString("ISBN"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setGenre(rs.getString("genre"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublishYear(rs.getInt("publishYear"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setDescription(rs.getString("description"));
        book.setAvailableCopies(rs.getInt("availableCopies"));
        return book;
    }
}
