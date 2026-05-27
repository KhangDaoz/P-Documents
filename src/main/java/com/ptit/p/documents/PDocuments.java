package com.ptit.p.documents;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.view.BookFrm;
import com.ptit.p.documents.view.LoginFrm;
import java.util.List;
import javax.swing.SwingUtilities;

public class PDocuments {

    public static void main(String[] args) {
        // Run standard team template code for Book
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.findAll();
            BookFrm bookView = new BookFrm();
            bookView.renderBookList(books);
        } catch (Exception e) {
            System.err.println("Standard book module is not fully loaded: " + e.getMessage());
        }

        // Run Kien's Admin User Management GUI entry point
        try {
            com.formdev.flatlaf.FlatIntelliJLaf.setup();
        } catch (Exception ex) {
            System.err.println("FlatLaf Look and Feel setup failed: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrm().setVisible(true);
        });
    }
}

