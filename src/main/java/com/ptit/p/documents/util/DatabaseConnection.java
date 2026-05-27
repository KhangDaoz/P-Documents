package com.ptit.p.documents.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton quản lý kết nối CSDL MySQL cho Library Management System.
 * Cách dùng:
 *   Connection conn = DatabaseConnection.getInstance().getConnection();
 */
public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/library_management?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    private static final String USER     = "root";
    private static final String PASSWORD = "123456";   // chỉnh theo môi trường

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {}

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Kết nối CSDL thành công.");
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
