package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO extends DAO {

    public BookDAO() {
        super();
    }

    
    
    private static final String AVAILABLE_COPIES_SUBQUERY = "(SELECT COUNT(*) FROM tblBookItem bi2"
            + " WHERE bi2.tblBookISBN = bk.ISBN"
            + " AND bi2.status = 'good'"
            + " AND bi2.ID NOT IN ("
            + "   SELECT bb2.tblBookItemID FROM tblBorrowedBook bb2"
            + "   JOIN tblBorrowing br2 ON bb2.tblBorrowingID = br2.ID"
            + "   WHERE br2.status IN ('pending','borrowed')"
            + " )) AS availableCopies";

    public Book findByID(String id) {
        String sql = "SELECT bk.ISBN, bk.title, bk.author, bk.genre,"
                + " bk.publisher, bk.publishYear, bk.price, bk.description, "
                + AVAILABLE_COPIES_SUBQUERY
                + " FROM tblBook bk WHERE bk.ISBN = ?";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Book> searchBook(String name, String author, String genre, String isbn) {
        ArrayList<Book> result = new ArrayList<>();
        String sql = "SELECT bk.ISBN, bk.title, bk.author, bk.genre,"
                + " bk.publisher, bk.publishYear, bk.price, bk.description, "
                + AVAILABLE_COPIES_SUBQUERY
                + " FROM tblBook bk"
                + " WHERE bk.title LIKE ? AND bk.author LIKE ?"
                + " AND bk.genre LIKE ? AND bk.ISBN LIKE ?";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
            ps.setString(1, "%" + (name != null ? name : "") + "%");
            ps.setString(2, "%" + (author != null ? author : "") + "%");
            ps.setString(3, "%" + (genre != null ? genre : "") + "%");
            ps.setString(4, "%" + (isbn != null ? isbn : "") + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    result.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Book> findAll() {
        ArrayList<Book> result = new ArrayList<>();
        String sql = "SELECT bk.ISBN, bk.title, bk.author, bk.genre,"
                + " bk.publisher, bk.publishYear, bk.price, bk.description, "
                + AVAILABLE_COPIES_SUBQUERY
                + " FROM tblBook bk";
        try (PreparedStatement ps = getCon().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                result.add(mapRow(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Book mapRow(ResultSet rs) throws Exception {
        Book book = new Book();
        book.setISBN(rs.getString("ISBN"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setGenre(rs.getString("genre"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublishYear(rs.getInt("publishYear"));
        
        
        try {
            book.setPrice(rs.getDouble("price"));
        } catch (Exception e) {
            book.setPrice(rs.getDouble("price")); 
        }
        book.setDescription(rs.getString("description"));
        book.setAvailableCopies(rs.getInt("availableCopies"));
        return book;
    }

    
    public boolean addBook(Book book) {
        
        String checkSql = "SELECT 1 FROM tblBook WHERE ISBN = ?";
        try (PreparedStatement checkPs = getCon().prepareStatement(checkSql)) {
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
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
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
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
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

    
    public boolean updateBook(Book book) {
        String sql = "UPDATE tblBook SET title = ?, author = ?, genre = ?, publisher = ?, "
                + "publishYear = ?, price = ?, description = ? WHERE ISBN = ?";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
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

    
    public boolean deleteBook(String isbn) {
        String deleteItemsSql = "DELETE FROM tblBookItem WHERE tblBookISBN = ?";
        String deleteBookSql = "DELETE FROM tblBook WHERE ISBN = ?";
        try (PreparedStatement itemPs = getCon().prepareStatement(deleteItemsSql);
                PreparedStatement bookPs = getCon().prepareStatement(deleteBookSql)) {
            itemPs.setString(1, isbn);
            itemPs.executeUpdate();

            bookPs.setString(1, isbn);
            return bookPs.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean checkBookStatus(String isbn, boolean includeHistory) {
        String sql = includeHistory
                ? "SELECT COUNT(*) AS cnt FROM tblBorrowedBook bb "
                        + "JOIN tblBookItem bi ON bb.tblBookItemID = bi.ID "
                        + "WHERE bi.tblBookISBN = ?"
                : "SELECT COUNT(*) AS cnt FROM tblBorrowing br "
                        + "JOIN tblBorrowedBook bb ON br.ID = bb.tblBorrowingID "
                        + "JOIN tblBookItem bi ON bb.tblBookItemID = bi.ID "
                        + "WHERE bi.tblBookISBN = ? AND br.status IN ('pending', 'borrowed', 'overdue')";
        try (PreparedStatement ps = getCon().prepareStatement(sql)) {
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
