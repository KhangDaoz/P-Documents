package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Book;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("B001", "Clean Code", "Robert C. Martin"));
        books.add(new Book("B002", "Effective Java", "Joshua Bloch"));
        return books;
    }
}
