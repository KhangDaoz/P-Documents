package com.ptit.p.documents.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Đại diện cho một đầu sách (book title) trong danh mục thư viện.
 * Tương ứng với bảng tblBook trong CSDL.
 */
public class Book {
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private String publisher;
    private int publishYear;
    private double price;
    private String description;
    private int availableCopies; // Thuộc tính dẫn xuất — tính động, không lưu trong DB
    private int totalCopies;
    private BookItem[] bookItems; // Các bản sao vật lý (nạp theo nhu cầu)
    private List<BookItem> items = new ArrayList<>(); // from master & sang branches

    public Book() {}

    /** Constructor rút gọn 2 tham số (từ sang branch) */
    public Book(String isbn, String title, String author, String genre) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    /** Constructor rút gọn 3 tham số (từ huy branch) */
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    /** Constructor đầy đủ các trường chính (không có totalCopies) */
    public Book(String isbn, String title, String author, String genre,
                String publisher, int publishYear, double price, String description) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.price = price;
        this.description = description;
    }

    /** Constructor đầy đủ từ huy branch */
    public Book(String isbn, String title, String author, String genre,
                String publisher, int publishYear, double price,
                String description, int availableCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.price = price;
        this.description = description;
        this.availableCopies = availableCopies;
    }

    public Book(String isbn, String title, String author, String genre,
                String publisher, int publishYear, double price,
                String description, int availableCopies, int totalCopies) {
        this(isbn, title, author, genre, publisher, publishYear, price, description, availableCopies);
        this.totalCopies = totalCopies;
    }

    public Book(String isbn, String title, String author, String genre,
                String publisher, int publishYear, double price,
                String description, int availableCopies, int totalCopies, List<BookItem> items) {
        this(isbn, title, author, genre, publisher, publishYear, price, description, availableCopies, totalCopies);
        this.setItems(items);
    }

    // -------- Getters & Setters --------

    /** Alias của getIsbn() — giữ tương thích với code cũ sử dụng getId() */
    public String getId() {
        return isbn;
    }

    public String getISBN() {
        return isbn;
    }

    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public BookItem[] getBookItems() {
        return bookItems;
    }

    public void setBookItems(BookItem[] bookItems) {
        this.bookItems = bookItems;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public List<BookItem> getItems() {
        return items;
    }

    public void setItems(List<BookItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public void addItem(BookItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    @Override
    public String toString() {
        return isbn + " - " + title + " (" + author + ")" +
               " [total=" + totalCopies + ", available=" + availableCopies + "]";
    }
}
