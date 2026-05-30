package com.ptit.p.documents.model;

/**
 * Bản sách - Tầng thực thể.
 * Ánh xạ bảng tblBookItem (schema p_documents).
 * ID là INT auto_increment, status là ENUM('good','damaged','lost').
 */
public class BookItem {
    private int id;
    private String status; // 'good' | 'damaged' | 'lost'
    private String bookISBN; // ISBN của đầu sách (tham chiếu tiện ích)

    public BookItem() {}

    public BookItem(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public BookItem(int id, String status, String bookISBN) {
        this.id = id;
        this.status = status;
        this.bookISBN = bookISBN;
    }

    // -------- Getters & Setters --------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTblBookISBN() {
        return bookISBN;
    }

    public void setTblBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    @Override
    public String toString() {
        return "BookItem{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", bookISBN='" + bookISBN + '\'' +
                '}';
    }
}
