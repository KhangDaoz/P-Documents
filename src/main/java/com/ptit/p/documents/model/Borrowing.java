package com.ptit.p.documents.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.ptit.p.documents.dao.BookItemDAO;

public class Borrowing {
    private int id;
    private LocalDate borrowDate;
    private LocalDate expectedReceiveDate;
    private LocalDate actualReceiveDate;
    private String status;
    private List<BorrowedBook> books;
    private Student student;
    private User user;

    public Borrowing() {
        this.books = new ArrayList<>();
    }

    public Borrowing(int id, LocalDate borrowDate, LocalDate expectedReceiveDate,
                     LocalDate actualReceiveDate, String status, List<BorrowedBook> books,
                     Student student, User user) {
        this.id = id;
        this.borrowDate = borrowDate;
        this.expectedReceiveDate = expectedReceiveDate;
        this.actualReceiveDate = actualReceiveDate;
        this.status = status;
        this.books = books != null ? books : new ArrayList<>();
        this.student = student;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<BorrowedBook> getBooks() {
        return books;
    }

    public void setBooks(List<BorrowedBook> books) {
        this.books = books != null ? books : new ArrayList<>();
    }

    public List<BorrowedBook> getBorrowedBooks()
    {
        return this.books;
    }

    public int getNumberOfBooks() {
        return books.size();
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
    //update existed BorrowedBook
    public void updateBorrowedBook(BorrowedBook borrowedBook) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == borrowedBook.getId()) {
                books.set(i, borrowedBook);
                break;
            }
        }
    }
    @Override
    public String toString(){
        //BorrowedBook ISBN:
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < books.size(); i++) {
            sb.append(new BookItemDAO().getBookISBN(books.get(i).getBookItem().getId()));
            if (i < books.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
