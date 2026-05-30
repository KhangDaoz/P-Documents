package com.ptit.p.documents.model;

import com.ptit.p.documents.dao.BookItemDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Đại diện cho một phiếu mượn (borrowing slip) trong hệ thống thư viện.
 * Tương ứng với bảng tblBorrowing trong CSDL.
 */
public class Borrowing {
    private int id;
    private Date expectedReceiveDate;
    private Date actualReceiveDate;
    private String note;
    private String status;
    private Date createdAt;
    private List<BorrowedBook> books;
    private Student student;
    private User user;

    public Borrowing() {
        this.books = new ArrayList<>();
    }

    public Borrowing(int id, Date expectedReceiveDate, Date actualReceiveDate, 
                     String status, List<BorrowedBook> books, Student student, User user) {
        this.id = id;
        this.expectedReceiveDate = expectedReceiveDate;
        this.actualReceiveDate = actualReceiveDate;
        this.status = status;
        this.books = books != null ? books : new ArrayList<>();
        this.student = student;
        this.user = user;
    }

    public Borrowing(Student student, User user, Date expectedReceiveDate, String note) {
        this.student = student;
        this.user = user;
        this.expectedReceiveDate = expectedReceiveDate;
        this.note = note;
        this.status = "pending";
        this.books = new ArrayList<>();
    }

    // Constructor for old UI
    public Borrowing(Student student, User user, Date borrowDate, Date expectedReceiveDate) {
        this.student = student;
        this.user = user;
        this.createdAt = borrowDate;
        this.expectedReceiveDate = expectedReceiveDate;
        this.status = "pending";
        this.books = new ArrayList<>();
    }

    // -------- Getters & Setters --------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getExpectedReceiveDate() {
        return expectedReceiveDate;
    }

    public void setExpectedReceiveDate(Date expectedReceiveDate) {
        this.expectedReceiveDate = expectedReceiveDate;
    }

    public Date getActualReceiveDate() {
        return actualReceiveDate;
    }

    public void setActualReceiveDate(Date actualReceiveDate) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Deprecated
    public Date getBorrowDate() {
        return createdAt;
    }

    @Deprecated
    public void setBorrowDate(Date borrowDate) {
        this.createdAt = borrowDate;
    }

    public List<BorrowedBook> getBooks() {
        return books;
    }

    public void setBooks(List<BorrowedBook> books) {
        this.books = books != null ? books : new ArrayList<>();
    }
    
    public List<BorrowedBook> getBorrowedBooks() {
        return this.books;
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

    @Override
    public String toString() {
        if (books == null || books.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        BookItemDAO dao = new BookItemDAO();
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getBookItem() != null) {
                sb.append(dao.getBookISBN(books.get(i).getBookItem().getId()));
            }
            if (i < books.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
