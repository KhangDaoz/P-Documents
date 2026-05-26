/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.ptit.p.documents;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.view.BookFrm;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class PDocuments {

    public static void main(String[] args) {
        BookDAO bookDAO = new BookDAO();
        List<Book> books = bookDAO.findAll();

        BookFrm bookView = new BookFrm();
        bookView.renderBookList(books);
    }
}
