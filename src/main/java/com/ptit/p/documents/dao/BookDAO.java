package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
public class BookDAO extends DAO {
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT ISBN, title, author FROM tblBook";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String id = resultSet.getString("ISBN");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                books.add(new Book(id, title, author));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return books;
    }
    public Book findByID(String id) {
        String sql = "SELECT ISBN, title, author FROM tblBook WHERE ISBN = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Book(
                        resultSet.getString("ISBN"),
                        resultSet.getString("title"),
                        resultSet.getString("author")
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
