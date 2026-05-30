package com.ptit.p.documents.model;

import com.ptit.p.documents.dao.BookItemDAO;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

/**
 * Đại diện cho một phiếu mượn (borrowing slip) trong hệ thống thư viện.
 * Tương ứng với bảng tblBorrowing trong CSDL.
 */
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

    /** Constructor rút gọn từ sang branch */
    public Borrowing(int id, Student student, LocalDate createdAt) {
        this.id = id;
        this.student = student;
        this.createdAt = createdAt;
        this.books = new ArrayList<>();
    }

    /** Constructors từ huy branch */
    public Borrowing(int id, LocalDate expectedReceiveDate, LocalDate actualReceiveDate, 
                     String status, List<BorrowedBook> books, Student student, User user) {
        this.id = id;
        this.expectedReceiveDate = expectedReceiveDate;
        this.actualReceiveDate = actualReceiveDate;
        this.status = status;
        this.books = books != null ? books : new ArrayList<>();
        this.student = student;
        this.user = user;
    }

    public Borrowing(Student student, User user, LocalDate expectedReceiveDate, String note) {
        this.student = student;
        this.user = user;
        this.expectedReceiveDate = expectedReceiveDate;
        this.note = note;
        this.status = "pending";
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

    // -------- Getters & Setters --------

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

    @Deprecated
    public LocalDate getBorrowDate() {
        return createdAt;
    }

    @Deprecated
    public void setBorrowDate(LocalDate borrowDate) {
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
