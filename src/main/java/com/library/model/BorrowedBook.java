package com.library.model;

import java.time.LocalDate;

/**
 * Chi tiết một dòng mượn - Tầng thực thể (spec §1.b bước 27-31).
 * Tham chiếu Borrowing + BookItem; cung cấp 6 cột hiển thị trong BorrowDetailFrm
 * (mã SV, tên, ngày mượn, hạn trả, ngày trả, trạng thái - spec §1.b bước 34).
 */
public class BorrowedBook {
    private Borrowing borrowing;
    private BookItem  bookItem;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String    status;

    public BorrowedBook() {}

    public BorrowedBook(Borrowing borrowing, BookItem bookItem,
                        LocalDate dueDate, LocalDate returnDate, String status) {
        this.borrowing  = borrowing;
        this.bookItem   = bookItem;
        this.dueDate    = dueDate;
        this.returnDate = returnDate;
        this.status     = status;
    }

    public Borrowing getBorrowing()           { return borrowing; }
    public void      setBorrowing(Borrowing b){ this.borrowing = b; }
    public BookItem  getBookItem()            { return bookItem; }
    public void      setBookItem(BookItem b)  { this.bookItem = b; }
    public LocalDate getDueDate()             { return dueDate; }
    public void      setDueDate(LocalDate d)  { this.dueDate = d; }
    public LocalDate getReturnDate()          { return returnDate; }
    public void      setReturnDate(LocalDate d){ this.returnDate = d; }
    public String    getStatus()              { return status; }
    public void      setStatus(String s)      { this.status = s; }
}
