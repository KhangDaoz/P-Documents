package com.ptit.p.documents.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Đầu sách - Tầng thực thể.
 * Sở hữu danh sách BookItem (bản sách).
 */
public class Book {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private List<BookItem> items = new ArrayList<>();

    public Book() {}

    public Book(String bookId, String title, String author, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
    }

    public String getBookId()           { return bookId; }
    public void   setBookId(String v)   { this.bookId = v; }
    public String getTitle()            { return title; }
    public void   setTitle(String v)    { this.title = v; }
    public String getAuthor()           { return author; }
    public void   setAuthor(String v)   { this.author = v; }
    public String getCategory()         { return category; }
    public void   setCategory(String v) { this.category = v; }

    public List<BookItem> getItems()              { return items; }
    public void           setItems(List<BookItem> v) { this.items = v; }
    public void           addItem(BookItem item)  { this.items.add(item); }
}
