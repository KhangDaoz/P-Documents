package com.ptit.p.documents.model;

/**
 * Bản sách - Tầng thực thể.
 * Ánh xạ bảng tblBookItem (schema p_documents).
 * ID là INT auto_increment, status là ENUM('good','damaged','lost').
 * Book là chủ sở hữu danh sách BookItem.
 */
public class BookItem {
    private int    id;       // tblBookItem.ID (int)
    private String status;   // 'good', 'damaged', 'lost'

    public BookItem() {}

    public BookItem(int id, String status) {
        this.id     = id;
        this.status = status;
    }

    public int    getId()             { return id; }
    public void   setId(int v)        { this.id = v; }
    public String getStatus()         { return status; }
    public void   setStatus(String v) { this.status = v; }
}
