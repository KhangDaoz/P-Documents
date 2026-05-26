package com.library.model;

import java.time.LocalDate;

/**
 * Thực thể báo cáo sách hư hỏng / thất lạc - Tầng thực thể (spec §2.a).
 * Đóng gói BookItem + lý do + ngày báo cáo (spec §2.b bước 14-18).
 */
public class StockStat {
    private BookItem  bookItem;
    private String    reason;        // "Hư hỏng" | "Thất lạc"
    private LocalDate reportedDate;

    public StockStat() {}

    public StockStat(BookItem bookItem, String reason, LocalDate reportedDate) {
        this.bookItem     = bookItem;
        this.reason       = reason;
        this.reportedDate = reportedDate;
    }

    public BookItem  getBookItem()              { return bookItem; }
    public void      setBookItem(BookItem b)    { this.bookItem = b; }
    public String    getReason()                { return reason; }
    public void      setReason(String r)        { this.reason = r; }
    public LocalDate getReportedDate()          { return reportedDate; }
    public void      setReportedDate(LocalDate d){ this.reportedDate = d; }
}
