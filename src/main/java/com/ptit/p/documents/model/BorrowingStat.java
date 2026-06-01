package com.ptit.p.documents.model;

public class BorrowingStat extends Book {
    private int borrowCount;

    public BorrowingStat() {
        super();
    }

    public BorrowingStat(String isbn, String title, String author, String genre, int borrowCount) {
        super(isbn, title, author, genre);
        this.borrowCount = borrowCount;
    }

    public int  getBorrowCount()       { return borrowCount; }
    public void setBorrowCount(int v)  { this.borrowCount = v; }
}
