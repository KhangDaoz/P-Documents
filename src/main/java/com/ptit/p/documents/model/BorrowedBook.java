package com.ptit.p.documents.model;

import java.time.LocalDate;

/**
 * Chi tiết một dòng mượn - Tầng thực thể.
 * Ánh xạ bảng tblBorrowedBook (schema p_documents).
 * Tham chiếu Borrowing + BookItem; cung cấp 6 cột hiển thị trong BorrowDetailFrm.
 */
public class BorrowedBook {
    private Borrowing borrowing;
    private BookItem  bookItem;
    private LocalDate expectedReturnDate;  // tblBorrowedBook.expectedReturnDate (trước: dueDate)
    private LocalDate actualReturnDate;    // tblBorrowedBook.actualReturnDate   (trước: returnDate)
    private String    status;

    public BorrowedBook() {}

    public BorrowedBook(Borrowing borrowing, BookItem bookItem,
                        LocalDate expectedReturnDate, LocalDate actualReturnDate, String status) {
        this.borrowing          = borrowing;
        this.bookItem           = bookItem;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate   = actualReturnDate;
        this.status             = status;
    }

    public Borrowing getBorrowing()                    { return borrowing; }
    public void      setBorrowing(Borrowing b)         { this.borrowing = b; }
    public BookItem  getBookItem()                     { return bookItem; }
    public void      setBookItem(BookItem b)           { this.bookItem = b; }
    public LocalDate getExpectedReturnDate()            { return expectedReturnDate; }
    public void      setExpectedReturnDate(LocalDate d) { this.expectedReturnDate = d; }
    public LocalDate getActualReturnDate()              { return actualReturnDate; }
    public void      setActualReturnDate(LocalDate d)   { this.actualReturnDate = d; }
    public String    getStatus()                        { return status; }
    public void      setStatus(String s)                { this.status = s; }
}
