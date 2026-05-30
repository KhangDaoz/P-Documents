package com.ptit.p.documents.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton quản lý kết nối CSDL MySQL cho Library Management System.
 * Kết nối tới database: p_documents.
 * Có cơ chế tự động thử mật khẩu '1812' trước, nếu lỗi sẽ thử '123456' (fallback).
 */
public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/p_documents?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true";
    private static final String USER = "root";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {}

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try {
                    // Thử kết nối với mật khẩu '1812' (môi trường của Huy)
                    connection = DriverManager.getConnection(URL, USER, "1812");
                    System.out.println("[DB] Kết nối CSDL thành công với mật khẩu '1812'.");
                } catch (SQLException ex) {
                    System.out.println("[DB] Thử kết nối với '1812' thất bại. Đang thử lại với mật khẩu '123456'...");
                    // Thử kết nối với mật khẩu '123456' (môi trường của Sang)
                    connection = DriverManager.getConnection(URL, USER, "123456");
                    System.out.println("[DB] Kết nối CSDL thành công với mật khẩu '123456'.");
                }
            } catch (ClassNotFoundException e) {
                throw new SQLException("Không tìm thấy MySQL JDBC Driver: " + e.getMessage(), e);
            }
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DB] Đã đóng kết nối CSDL.");
            } catch (SQLException ignored) {}
        }
    }
}
