package com.library.model;

/**
 * Đầu sách - Tầng thực thể (spec §1.a, §2.a).
 * Thuộc tính theo spec §1.b bước 16: title, author, category (+ bookId là khóa).
 */
public class Book {
    private String bookId;
    private String title;
    private String author;
    private String category;

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
}
