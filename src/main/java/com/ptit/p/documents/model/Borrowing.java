package com.ptit.p.documents.model;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

public class Borrowing {
    private int id;
    private LocalDate expectedReceiveDate;
    private LocalDate actualReceiveDate;
    private String note;
    private String status;
    private LocalDate createdAt;
    private List<BorrowedBook> books = new ArrayList<>();
    private Student student;
    private User user;

    public Borrowing() {
        this.books = new ArrayList<>();
    }

    public Borrowing(Student student, User user, LocalDate borrowDate, LocalDate expectedReceiveDate) {
        this.student = student;
        this.user = user;
        this.createdAt = borrowDate;
        this.expectedReceiveDate = expectedReceiveDate;
        this.status = "pending";
        this.books = new ArrayList<>();
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getExpectedReceiveDate() {
        return expectedReceiveDate;
    }

    public void setExpectedReceiveDate(LocalDate expectedReceiveDate) {
        this.expectedReceiveDate = expectedReceiveDate;
    }

    public LocalDate getActualReceiveDate() {
        return actualReceiveDate;
    }

    public void setActualReceiveDate(LocalDate actualReceiveDate) {
        this.actualReceiveDate = actualReceiveDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    
    public LocalDate getBorrowDate() {
        return createdAt;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.createdAt = borrowDate;
    }

    public List<BorrowedBook> getBooks() {
        return books;
    }

    public void setBooks(List<BorrowedBook> books) {
        this.books = books != null ? books : new ArrayList<>();
    }

    public int getNumberOfBooks() {
        return books != null ? books.size() : 0;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateBorrowedBook(BorrowedBook borrowedBook) {
        if (books == null) return;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == borrowedBook.getId()) {
                books.set(i, borrowedBook);
                break;
            }
        }
    }
}
