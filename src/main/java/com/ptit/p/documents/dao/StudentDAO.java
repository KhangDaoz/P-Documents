package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Student;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * DAO xử lý các thao tác truy xuất CSDL liên quan đến sinh viên (Student).
 * Kế thừa lớp DAO để sử dụng kết nối CSDL dùng chung.
 *
 * Các phương thức:
 *   - searchStudent() : tìm sinh viên theo mã SV hoặc họ tên
 *   - addStudent()    : thêm sinh viên mới vào CSDL
 *
 * Lưu ý: tblStudent.ID là VARCHAR(20) — mã sinh viên do thủ thư nhập tay
 * (ví dụ: "SV220134"). Không phải AUTO_INCREMENT.
 */
public class StudentDAO extends DAO {

    public StudentDAO() {
        super();
    }

    /**
     * Tìm kiếm sinh viên theo mã sinh viên hoặc họ tên (LIKE — không phân biệt hoa thường).
     *
     * SQL tương ứng:
     *   SELECT * FROM tblStudent WHERE ID LIKE ? OR fullName LIKE ?
     *
     * @param key Từ khóa tìm kiếm (mã SV hoặc một phần họ tên)
     * @return Danh sách sinh viên phù hợp
     */
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

    /**
     * Thêm một sinh viên mới vào CSDL.
     * Mã sinh viên (studentId) do thủ thư nhập tay — phải là VARCHAR duy nhất.
     *
     * SQL tương ứng:
     *   INSERT INTO tblStudent(ID, fullName, email, phone, address) VALUES(?,?,?,?,?)
     *
     * @param s Đối tượng Student cần thêm (studentId phải đã được set)
     * @return true nếu thêm thành công, false nếu xảy ra lỗi (ví dụ mã SV đã tồn tại)
     */
    public boolean addStudent(Student s) {
        String sql = "INSERT INTO tblStudent(ID, fullName, email, phone, address)"
                   + " VALUES(?,?,?,?,?)";
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
