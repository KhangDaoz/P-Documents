package com.ptit.p.documents.model;

/**
 * Bản sách - Tầng thực thể.
 * Chỉ gồm id và status (missing, damaged). Book là chủ sở hữu danh sách BookItem.
 */
public class BookItem {
    private String id;
    private String status;

    public BookItem() {}

    public BookItem(String id, String status) {
        this.id     = id;
        this.status = status;
    }

    public String getId()             { return id; }
    public void   setId(String v)     { this.id = v; }
    public String getStatus()         { return status; }
    public void   setStatus(String v) { this.status = v; }
}
