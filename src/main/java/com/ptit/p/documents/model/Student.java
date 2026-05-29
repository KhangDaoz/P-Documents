package com.ptit.p.documents.model;

/**
 * Sinh viên - Tầng thực thể.
 * Ánh xạ bảng tblStudent (schema p_documents).
 * ID là INT auto_increment.
 */
public class Student {
    private int    id;         // tblStudent.ID (int)
    private String fullName;
    private String email;
    private String phone;
    private String address;

    public Student() {}

    public Student(int id, String fullName) {
        this.id       = id;
        this.fullName = fullName;
    }

    public Student(int id, String fullName, String email, String phone, String address) {
        this.id       = id;
        this.fullName = fullName;
        this.email    = email;
        this.phone    = phone;
        this.address  = address;
    }

    public int    getId()              { return id; }
    public void   setId(int v)         { this.id = v; }
    public String getFullName()        { return fullName; }
    public void   setFullName(String v){ this.fullName = v; }
    public String getEmail()           { return email; }
    public void   setEmail(String v)   { this.email = v; }
    public String getPhone()           { return phone; }
    public void   setPhone(String v)   { this.phone = v; }
    public String getAddress()         { return address; }
    public void   setAddress(String v) { this.address = v; }
}
