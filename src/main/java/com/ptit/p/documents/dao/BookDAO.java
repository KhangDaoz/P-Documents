package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BookDAO extends DAO {

    public BookDAO() {
        super();
    }

    // availableCopies: đếm BookItem status='good' không trong phiếu pending/borrowed
    private static final String AVAILABLE_COPIES_SUBQUERY =
            "(SELECT COUNT(*) FROM tblBookItem bi2"
            + " WHERE bi2.tblBookISBN = bk.ISBN"
            + " AND bi2.status = 'good'"
            + " AND bi2.ID NOT IN ("
            + "   SELECT bb2.tblBookItemID FROM tblBorrowedBook bb2"
            + "   JOIN tblBorrowing br2 ON bb2.tblBorrowingID = br2.ID"
            + "   WHERE br2.status IN ('pending','borrowed')"
            + " )) AS availableCopies";

    public ArrayList<Book> searchBook(String name, String author, String genre, String isbn) {
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
            while (rs.next()) result.add(mapRow(rs));
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
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(mapRow(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

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
