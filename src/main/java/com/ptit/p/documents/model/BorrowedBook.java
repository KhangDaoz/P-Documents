package com.ptit.p.documents.model;

import java.util.ArrayList;
import java.time.LocalDate;

public class BorrowedBook {
    private int id;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private String status;
    private String note;
    private double price;
    private BookItem bookItem;
    private Book book;
    private ArrayList<BorrowedBookFine> borrowedBookFine;
    private Borrowing borrowing;

    public BorrowedBook() {
        this.borrowedBookFine = new ArrayList<>();
    }

    public BorrowedBook(int id, LocalDate expectedReturnDate, LocalDate actualReturnDate, String status) {
        this.id = id;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.borrowedBookFine = new ArrayList<>();
    }

    public BorrowedBook(Book book, LocalDate expectedReturnDate, double price) {
        this.book = book;
        this.expectedReturnDate = expectedReturnDate;
        this.price = price;
        this.status = "good";
        this.borrowedBookFine = new ArrayList<>();
    }

    public BorrowedBook(Book book, LocalDate expectedReturnDate, java.math.BigDecimal price) {
        this.book = book;
        this.expectedReturnDate = expectedReturnDate;
        this.price = price.doubleValue();
        this.status = "good";
        this.borrowedBookFine = new ArrayList<>();
    }

    public BorrowedBook(int id, LocalDate expectedReturnDate, LocalDate actualReturnDate, String status,
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

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Borrowing getBorrowing() {
        return borrowing;
    }

    public void setBorrowing(Borrowing borrowing) {
        this.borrowing = borrowing;
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
