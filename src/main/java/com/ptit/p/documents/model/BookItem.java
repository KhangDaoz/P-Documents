package com.ptit.p.documents.model;

public class BookItem {
    private int id;
    private String status; 
    private String bookISBN; 

    public BookItem() {}

    public BookItem(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public BookItem(int id, String status, String bookISBN) {
        this.id = id;
        this.status = status;
        this.bookISBN = bookISBN;
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

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    @Override
    public String toString() {
        return "BookItem{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", bookISBN='" + bookISBN + '\'' +
                '}';
    }
}
