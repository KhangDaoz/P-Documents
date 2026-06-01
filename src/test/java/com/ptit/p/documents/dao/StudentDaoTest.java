package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Student;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;

public class StudentDaoTest {

    StudentDAO sd = new StudentDAO();

    
    @Test
    public void testSearchStudentStandard1() {
        String key = "SV001";
        ArrayList<Student> list = sd.searchStudent(key);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("SV001", list.get(0).getStudentId());
    }

    
    @Test
    public void testSearchStudentException1() {
        String key = "XXXXXXXXXX";
        ArrayList<Student> list = sd.searchStudent(key);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    
    @Test
    public void testAddStudentStandard() {
        Connection con = sd.getCon();
        try {
            con.setAutoCommit(false);

            Student s = new Student("SV999999", "Nguyen Test", "test@ptit.edu.vn", "0900000000", "Ha Noi");
            boolean ok = sd.addStudent(s);
            Assert.assertTrue(ok);

            
            ArrayList<Student> list = sd.searchStudent("SV999999");
            Assert.assertEquals(1, list.size());
            Assert.assertEquals("SV999999", list.get(0).getStudentId());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { con.rollback(); con.setAutoCommit(true); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    
    @Test
    public void testAddStudentException() {
        Connection con = sd.getCon();
        try {
            con.setAutoCommit(false);

            
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
