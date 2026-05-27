package com.ptit.p.documents.view;

import com.ptit.p.documents.model.Book;
import java.util.List;

public class BookFrm {
    public void renderBookList(List<Book> books) {
        System.out.println("=== DANH SACH SACH ===");
        for (Book book : books) {
            System.out.println(book.getId() + " | " + book.getTitle() + " | " + book.getAuthor());
        }
    }
}
