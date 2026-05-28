package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Student;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class StudentDAO extends DAO {

    public StudentDAO() {
        super();
    }

    public ArrayList<Student> searchStudent(String key) {
        ArrayList<Student> result = new ArrayList<>();
        String sql = "SELECT * FROM tblStudent WHERE ID LIKE ? OR fullName LIKE ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            String pattern = "%" + key + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getString("ID"));
                student.setFullName(rs.getString("fullName"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));
                student.setAddress(rs.getString("address"));
                result.add(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean addStudent(Student s) {
        String sql = "INSERT INTO tblStudent(ID, fullName, email, phone, address) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getAddress());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
