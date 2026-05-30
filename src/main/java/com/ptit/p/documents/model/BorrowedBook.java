package com.ptit.p.documents.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class BorrowedBook {
    private int id;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private String status;
    private String note;
    private double price;
    private BookItem bookItem;
    private ArrayList<BorrowedBookFine> borrowedBookFine;

    public BorrowedBook() {
    }

    public BorrowedBook(int id, LocalDate expectedReturnDate, LocalDate actualReturnDate, String status,
                        String note, double price, BookItem bookItem, ArrayList<BorrowedBookFine> borrowedBookFines) {
        this.id = id;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.note = note;
        this.price = price;
        this.bookItem = bookItem;
        this.borrowedBookFine = borrowedBookFine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
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

    public ArrayList<BorrowedBookFine> getBorrowedBookFines() {
        return borrowedBookFine;
    }

    public void addBorrowedBookFine(BorrowedBookFine borrowedBookFine) {
        if(this.borrowedBookFine == null) {
            this.borrowedBookFine = new ArrayList<>();
        }
        this.borrowedBookFine.add(borrowedBookFine);
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
