package com.ptit.p.documents.model;

import java.time.LocalDate;

/**
 * Thực thể báo cáo sách hư hỏng / thất lạc - Tầng thực thể.
 * Tham chiếu Book (chứa danh sách BookItem) + lý do + ngày báo cáo.
 * Modul 2 lấy dữ liệu từ tblBookItem.status ('damaged'/'lost')
 * và tblBookItem.updatedAt làm ngày báo cáo.
 * BookItem truy cập qua book.getItems() theo đúng chuỗi gọi:
 *   StockStat -> Book -> BookItem.
 */
public class StockStat {
    private Book      book;
    private String    reason;        // "Hư hỏng" | "Thất lạc" (hiển thị tiếng Việt)
    private LocalDate reportedDate;  // tblBookItem.updatedAt

    public StockStat() {}

    public StockStat(Book book, String reason, LocalDate reportedDate) {
        this.book         = book;
        this.reason       = reason;
        this.reportedDate = reportedDate;
    }

    public Book      getBook()                  { return book; }
    public void      setBook(Book b)            { this.book = b; }
    public String    getReason()                { return reason; }
    public void      setReason(String r)        { this.reason = r; }
    public LocalDate getReportedDate()          { return reportedDate; }
    public void      setReportedDate(LocalDate d){ this.reportedDate = d; }
}
