package com.ptit.p.documents.model;

import java.math.BigDecimal;

/**
 * Đại diện cho một đầu sách (book title) trong danh mục thư viện.
 * Tương ứng với bảng tblBook trong CSDL.
 * Một đầu sách (Book) có thể có nhiều bản sao vật lý (BookItem).
 *
 * Trường isbn là khóa chính, ví dụ: "978-604-1-01234-5".
 *
 * Lưu ý: availableCopies là thuộc tính DẪN XUẤT — không có trong CSDL,
 * được tính động bởi BookDAO bằng cách đếm số BookItem có status='good'
 * và không đang nằm trong phiếu mượn active (pending/borrowed).
 */
public class Book {
    private String     isbn;           // Khóa chính
    private String     title;
    private String     author;
    private String     genre;
    private String     publisher;
    private int        publishYear;
    private BigDecimal price;
    private String     description;
    private int        availableCopies; // Thuộc tính dẫn xuất — tính động, không lưu trong DB
    private BookItem[] bookItems;       // Các bản sao vật lý (nạp theo nhu cầu)

    public Book() {}

    /**
     * Constructor rút gọn — giữ tương thích với code cũ (BookFrm).
     */
    public Book(String isbn, String title, String author) {
        this.isbn   = isbn;
        this.title  = title;
        this.author = author;
    }

    /**
     * Constructor đầy đủ tất cả các trường chính.
     */
    public Book(String isbn, String title, String author, String genre,
                String publisher, int publishYear, BigDecimal price,
                String description, int availableCopies) {
        this.isbn            = isbn;
        this.title           = title;
        this.author          = author;
        this.genre           = genre;
        this.publisher       = publisher;
        this.publishYear     = publishYear;
        this.price           = price;
        this.description     = description;
        this.availableCopies = availableCopies;
    }

    // -------- Getters & Setters --------

    /** Alias của getIsbn() — giữ tương thích với code cũ sử dụng getId(). */
    public String getId() {
        return isbn;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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

    @Override
    public String toString() {
        return isbn + " - " + title + " (" + author + ")";
    }
}
