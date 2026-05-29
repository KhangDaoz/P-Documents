package com.ptit.p.documents.model;

import java.time.LocalDate;

/**
 * Lượt mượn (header) - Tầng thực thể.
 * Ánh xạ bảng tblBorrowing (schema p_documents).
 * Dùng createdAt thay cho borrow_date (cột không còn trong schema final).
 */
public class Borrowing {
    private int       id;          // tblBorrowing.ID
    private Student   student;
    private LocalDate createdAt;   // thay thế borrow_date; ngày tạo phiếu mượn

    public Borrowing() {}

    public Borrowing(int id, Student student, LocalDate createdAt) {
        this.id        = id;
        this.student   = student;
        this.createdAt = createdAt;
    }

    public int       getId()                   { return id; }
    public void      setId(int v)              { this.id = v; }
    public Student   getStudent()              { return student; }
    public void      setStudent(Student s)     { this.student = s; }
    public LocalDate getCreatedAt()            { return createdAt; }
    public void      setCreatedAt(LocalDate d) { this.createdAt = d; }
}
