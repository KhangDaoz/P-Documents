package com.ptit.p.documents.model;

public class Student {
    private String studentId;
    private String fullName;
    private String email;
    private String phone;
    private String address;

    public Student() {
    }

    public Student(String studentId, String fullName) {
        this.studentId = studentId;
        this.fullName = fullName;
    }

    public Student(String studentId, String fullName, String email,
            String phone, String address) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getId() {
        return studentId;
    }

    public void setId(String id) {
        this.studentId = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return studentId + " - " + fullName;
    }
}
