package com.ptit.p.documents.model;

import java.time.LocalDate;

public class StockStat {
    private Book      book;
    private String    reason;        
    private LocalDate reportedDate;  

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
