package com.ptit.p.documents.dao;

import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Student;
import com.ptit.p.documents.model.User;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO extends DAO {
    public List<Borrowing> searchBorrowing(String studentId, String studentName, String... statuses) {
        List<Borrowing> results = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b.ID, b.expectedReceiveDate, b.actualReceiveDate, b.status, b.createdAt, ")
           .append("s.ID AS studentId, s.fullName AS studentName, s.email, s.phone AS studentPhone, s.address, ")
           .append("u.ID AS userId, u.username, u.password, u.fullName AS userFullName, u.phone AS userPhone, u.role ")
           .append("FROM tblBorrowing b ")
           .append("JOIN tblStudent s ON b.tblStudentID = s.ID ")
           .append("JOIN tblUser u ON b.tblUserID = u.ID ")
           .append("WHERE 1=1 ");


        List<Object> params = new ArrayList<>();
        
        // if (statuses != null && statuses.length > 0) {
        //     sql.append("AND b.status IN (");
        //     for (int i = 0; i < statuses.length; i++) {
        //         sql.append("?");
        //         if (i < statuses.length - 1) sql.append(", ");
        //         params.add(statuses[i]);
        //     }
        //     sql.append(") ");
        // }

        if (studentId != null && !studentId.isBlank()) {
            sql.append("AND s.ID = ? ");
            params.add(studentId);
        }
        if (studentName != null && !studentName.isBlank()) {
            sql.append("AND s.fullName LIKE ? ");
            params.add("%" + studentName + "%");
        }
        sql.append(";");

        

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Borrowing borrowing = new Borrowing();
                    borrowing.setId(resultSet.getInt("ID"));
                    borrowing.setExpectedReceiveDate(toLocalDate(resultSet.getDate("expectedReceiveDate")));
                    borrowing.setActualReceiveDate(toLocalDate(resultSet.getDate("actualReceiveDate")));
                    borrowing.setStatus(resultSet.getString("status"));

                    Timestamp createdAt = resultSet.getTimestamp("createdAt");
                    if (createdAt != null) {
                        borrowing.setBorrowDate(createdAt.toLocalDateTime().toLocalDate());
                    }

                    Student student = new Student();
                    student.setId(resultSet.getString("studentId"));
                    student.setFullName(resultSet.getString("studentName"));
                    student.setEmail(resultSet.getString("email"));
                    student.setPhone(resultSet.getString("studentPhone"));
                    student.setAddress(resultSet.getString("address"));
                    borrowing.setStudent(student);
                    System.out.println("added Student: " + student.getFullName()  +" to borrowing");
                    User user = new User();
                    user.setId(resultSet.getInt("userId"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setFullName(resultSet.getString("userFullName"));
                    user.setPhone(resultSet.getString("userPhone"));
                    user.setRole(resultSet.getString("role"));
                    borrowing.setUser(user);
                    System.out.println("added User: " + user.getFullName()  +" to borrowing");

                    //Get BorrowedBooks
                    List<BorrowedBook> books = loadBorrowedBooks(borrowing.getId());
                    borrowing.setBooks(books);
                    System.out.println("added " + books.size() + " books to borrowing");

                    results.add(borrowing);
                    
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return results;
    }

    public boolean updateBorrowingStatus(int borrowingId, String status) {
        String sql = "UPDATE tblBorrowing SET status = ? WHERE ID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, borrowingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateBorrowingStatus(int borrowingId, java.time.LocalDate actualReceiveDate, String status) {
        String sql = "UPDATE tblBorrowing SET actualReceiveDate = ?, status = ? WHERE ID = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, actualReceiveDate != null ? java.sql.Date.valueOf(actualReceiveDate) : null);
            statement.setString(2, status);
            statement.setInt(3, borrowingId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<BorrowedBook> loadBorrowedBooks(int borrowingId) {
        return new BorrowedBookDAO().findByBorrowingId(borrowingId);
    }

    private java.time.LocalDate toLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

}
