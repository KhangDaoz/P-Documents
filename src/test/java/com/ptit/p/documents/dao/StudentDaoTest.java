package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Student;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;

public class StudentDaoTest {

    StudentDAO sd = new StudentDAO();

    // TT7: Tim sinh vien ton tai theo ma SV
    @Test
    public void testSearchStudentStandard1() {
        String key = "SV001";
        ArrayList<Student> list = sd.searchStudent(key);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("SV001", list.get(0).getStudentId());
    }

    // TT8: Tim sinh vien khong ton tai
    @Test
    public void testSearchStudentException1() {
        String key = "XXXXXXXXXX";
        ArrayList<Student> list = sd.searchStudent(key);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    // TT9: Them sinh vien moi hop le (rollback sau test)
    @Test
    public void testAddStudentStandard() {
        Connection con = sd.getCon();
        try {
            con.setAutoCommit(false);

            Student s = new Student("SV999999", "Nguyen Test", "test@ptit.edu.vn", "0900000000", "Ha Noi");
            boolean ok = sd.addStudent(s);
            Assert.assertTrue(ok);

            // Kiem tra da them vao CSDL chua
            ArrayList<Student> list = sd.searchStudent("SV999999");
            Assert.assertEquals(1, list.size());
            Assert.assertEquals("SV999999", list.get(0).getStudentId());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { con.rollback(); con.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // TT10: Them sinh vien trung ma — phai that bai (rollback sau test)
    @Test
    public void testAddStudentException() {
        Connection con = sd.getCon();
        try {
            con.setAutoCommit(false);

            // SV001 da co san trong seed data
            Student s = new Student("SV001", "Trung Ma SV", "trung@ptit.edu.vn", "0911111111", "Ha Noi");
            boolean ok = sd.addStudent(s);
            Assert.assertFalse(ok);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { con.rollback(); con.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
