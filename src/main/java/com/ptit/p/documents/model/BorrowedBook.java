package com.ptit.p.documents.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Đại diện cho một cuốn sách cụ thể trong một phiếu mượn.
 * Tương ứng với bảng tblBorrowedBook trong CSDL.
 */
public class BorrowedBook {
    private int id;
    private Date expectedReturnDate;
    private Date actualReturnDate;
    private String status;
    private String note;
    private double price;
    private BookItem bookItem;
    private Book book;
    private ArrayList<BorrowedBookFine> borrowedBookFine;

    public BorrowedBook() {
        this.borrowedBookFine = new ArrayList<>();
    }

    public BorrowedBook(Book book, Date expectedReturnDate, double price) {
        this.book = book;
        this.expectedReturnDate = expectedReturnDate;
        this.price = price;
        this.status = "good";
        this.borrowedBookFine = new ArrayList<>();
    }

    public BorrowedBook(int id, Date expectedReturnDate, Date actualReturnDate, String status,
                        String note, double price, BookItem bookItem, ArrayList<BorrowedBookFine> borrowedBookFine) {
        this.id = id;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.note = note;
        this.price = price;
        this.bookItem = bookItem;
        this.borrowedBookFine = borrowedBookFine != null ? borrowedBookFine : new ArrayList<>();
    }

    // -------- Getters & Setters --------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(Date expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public Date getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(Date actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public BookItem getBookItem() {
        return bookItem;
    }

    public void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public ArrayList<BorrowedBookFine> getBorrowedBookFines() {
        return borrowedBookFine;
    }

    public void addBorrowedBookFine(BorrowedBookFine fine) {
        if(this.borrowedBookFine == null) {
            this.borrowedBookFine = new ArrayList<>();
        }
        this.borrowedBookFine.add(fine);
    }

    public void setBorrowedBookFines(ArrayList<BorrowedBookFine> borrowedBookFines) {
        if (borrowedBookFines == null) {
            this.borrowedBookFine = new ArrayList<>();
        } else {
            this.borrowedBookFine.clear();
            this.borrowedBookFine.addAll(borrowedBookFines);
        }
    }
}
