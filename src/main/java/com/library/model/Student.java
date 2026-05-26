package com.library.model;

/**
 * Sinh viên - Tầng thực thể (spec §1.b bước 29).
 * Thuộc tính: studentCode, fullName.
 */
public class Student {
    private String studentCode;
    private String fullName;

    public Student() {}

    public Student(String studentCode, String fullName) {
        this.studentCode = studentCode;
        this.fullName    = fullName;
    }

    public String getStudentCode()         { return studentCode; }
    public void   setStudentCode(String v) { this.studentCode = v; }
    public String getFullName()            { return fullName; }
    public void   setFullName(String v)    { this.fullName = v; }
}
