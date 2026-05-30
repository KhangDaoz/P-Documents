package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp DAO cơ sở — quản lý kết nối CSDL dùng chung cho tất cả các lớp DAO.
 * Tất cả các lớp DAO cụ thể (BookDAO, StudentDAO, BorrowingDAO) phải kế thừa lớp này.
 *
 * Cấu hình kết nối:
 *   URL  : jdbc:mysql://localhost:3306/p_documents
 *   User : root
 *   Pass : (thay đổi theo môi trường thực tế — xem DB_PASSWORD)
 */
public class DAO {
    protected Connection con;

    // -------- Thông tin kết nối CSDL --------
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/p_documents"
            + "?useSSL=false"
            + "&serverTimezone=UTC"
            + "&characterEncoding=UTF-8"
            + "&allowPublicKeyRetrieval=true";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "1812"; // Giữ mật khẩu 1812 cho môi trường hiện tại

    public DAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish DB connection", e);
        }
    }

    public Connection getConnection() {
        return this.con;
    }

    public Connection getCon() {
        return this.con;
    }
}
