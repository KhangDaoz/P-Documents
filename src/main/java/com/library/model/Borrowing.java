package com.library.model;

import java.time.LocalDate;

/**
 * Lượt mượn (header) - Tầng thực thể (spec §1.b bước 28-30).
 * Chứa tham chiếu Student và ngày mượn.
 */
public class Borrowing {
    private int       borrowId;
    private Student   student;
    private LocalDate borrowDate;

    public Borrowing() {}

    public Borrowing(int borrowId, Student student, LocalDate borrowDate) {
        this.borrowId   = borrowId;
        this.student    = student;
        this.borrowDate = borrowDate;
    }

    public int       getBorrowId()            { return borrowId; }
    public void      setBorrowId(int v)       { this.borrowId = v; }
    public Student   getStudent()             { return student; }
    public void      setStudent(Student s)    { this.student = s; }
    public LocalDate getBorrowDate()          { return borrowDate; }
    public void      setBorrowDate(LocalDate d) { this.borrowDate = d; }
}
