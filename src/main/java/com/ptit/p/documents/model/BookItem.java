package com.ptit.p.documents.model;

public class BookItem {
    private int id;
    private String status;
    // private String BookISBN;
    public BookItem() {
    }

    public BookItem(int id, String status) {
        this.id = id;
        this.status = status;
        // this.BookISBN = bookISBN;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // public String getBookISBN() {
    //     return BookISBN;
    // }

    // public void setBookISBN(String BookISBN) {
    //     this.BookISBN = BookISBN;
    // }

}
