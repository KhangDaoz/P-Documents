package com.ptit.p.documents.model;

public class BorrowingStat {
    private Book book;
    private int borrowCount;

    public BorrowingStat() {}

    public BorrowingStat(Book book, int borrowCount) {
        this.book = book;
        this.borrowCount = borrowCount;
    }

    public Book getBook()              { return book; }
    public void setBook(Book book)     { this.book = book; }
    public int  getBorrowCount()       { return borrowCount; }
    public void setBorrowCount(int v)  { this.borrowCount = v; }

    // Convenience accessors delegating to the associated Book
    public String getIsbn()   { return book != null ? book.getIsbn()   : null; }
    public String getTitle()  { return book != null ? book.getTitle()  : null; }
    public String getAuthor() { return book != null ? book.getAuthor() : null; }
    public String getGenre()  { return book != null ? book.getGenre()  : null; }
}
