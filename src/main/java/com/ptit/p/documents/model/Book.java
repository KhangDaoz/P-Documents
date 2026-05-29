package com.ptit.p.documents.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Đầu sách - Tầng thực thể.
 * Ánh xạ bảng tblBook (schema p_documents).
 * Sở hữu danh sách BookItem (bản sách).
 */
public class Book {
    private String isbn;       // PK: tblBook.ISBN
    private String title;
    private String author;
    private String genre;      // tblBook.genre (trước đây: category)
    private String publisher;
    private int    publishYear;
    private double price;
    private String description;
    private List<BookItem> items = new ArrayList<>();

    public Book() {}

    public Book(String isbn, String title, String author, String genre) {
        this.isbn  = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public Book(String isbn, String title, String author, String genre,
                String publisher, int publishYear, double price, String description) {
        this.isbn        = isbn;
        this.title       = title;
        this.author      = author;
        this.genre       = genre;
        this.publisher   = publisher;
        this.publishYear = publishYear;
        this.price       = price;
        this.description = description;
    }

    public String getIsbn()                { return isbn; }
    public void   setIsbn(String v)        { this.isbn = v; }
    public String getTitle()               { return title; }
    public void   setTitle(String v)       { this.title = v; }
    public String getAuthor()              { return author; }
    public void   setAuthor(String v)      { this.author = v; }
    public String getGenre()               { return genre; }
    public void   setGenre(String v)       { this.genre = v; }
    public String getPublisher()           { return publisher; }
    public void   setPublisher(String v)   { this.publisher = v; }
    public int    getPublishYear()         { return publishYear; }
    public void   setPublishYear(int v)    { this.publishYear = v; }
    public double getPrice()               { return price; }
    public void   setPrice(double v)       { this.price = v; }
    public String getDescription()         { return description; }
    public void   setDescription(String v) { this.description = v; }

    public List<BookItem> getItems()              { return items; }
    public void           setItems(List<BookItem> v) { this.items = v; }
    public void           addItem(BookItem item)  { this.items.add(item); }
}
