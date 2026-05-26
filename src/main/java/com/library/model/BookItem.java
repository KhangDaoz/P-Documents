package com.library.model;

/**
 * Bản sách - Tầng thực thể (spec §2.a, §2.b bước 15-18).
 * Đóng gói barcode + status + tham chiếu Book.
 */
public class BookItem {
    private String barcode;
    private String status;
    private Book   book;

    public BookItem() {}

    public BookItem(String barcode, String status, Book book) {
        this.barcode = barcode;
        this.status  = status;
        this.book    = book;
    }

    public String getBarcode()         { return barcode; }
    public void   setBarcode(String v) { this.barcode = v; }
    public String getStatus()          { return status; }
    public void   setStatus(String v)  { this.status = v; }
    public Book   getBook()            { return book; }
    public void   setBook(Book b)      { this.book = b; }
}
