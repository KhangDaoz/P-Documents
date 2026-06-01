package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp DAO cơ sở — và là NƠI DUY NHẤT trong toàn hệ thống mở kết nối tới CSDL.
 *
 * Mọi lớp DAO cụ thể kế thừa lớp này và dùng chung một {@link Connection} tĩnh.
 * Không có lớp nào khác (view, PDocuments, util...) được phép tự mở kết nối JDBC —
 * tất cả đều đi qua đây.
 *
 * Lưu ý: schema và dữ liệu mẫu nằm trong src/main/resources/schema.sql.
 * Hãy chạy file đó trong MySQL một lần trước khi khởi chạy ứng dụng.
 */
public class DAO {

    // ---- Cấu hình kết nối (chỉ khai báo ở đây) ----
    private static final String URL =
            "jdbc:mysql://localhost:3306/p_documents"
            + "?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    // Thử mật khẩu '1812' (môi trường của Huy) trước, sau đó '123456' (môi trường của Sang).
    private static final String[] PASSWORDS = { "1812", "123456" };

    /** Kết nối dùng chung cho toàn bộ các lớp DAO. */
    private static Connection sharedConnection;

    protected Connection con;

    public DAO() {
        try {
            this.con = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể thiết lập kết nối CSDL trong DAO", e);
        }
    }

    /**
     * Trả về kết nối dùng chung, tự mở lại nếu chưa có hoặc đã đóng.
     * Đây là điểm vào duy nhất để lấy {@link Connection}.
     */
    public Connection getConnection() throws SQLException {
        if (sharedConnection == null || sharedConnection.isClosed()) {
            sharedConnection = openConnection();
        }
        return sharedConnection;
    }

    /** Alias giữ tương thích với code cũ. */
    public Connection getCon() {
        try {
            return getConnection();
        } catch (SQLException e) {
            return this.con;
        }
    }

    private static Connection openConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không tìm thấy MySQL JDBC Driver: " + e.getMessage(), e);
        }
        SQLException last = null;
        for (String pwd : PASSWORDS) {
            try {
                Connection c = DriverManager.getConnection(URL, USER, pwd);
                System.out.println("[DB] Kết nối CSDL p_documents thành công.");
                return c;
            } catch (SQLException ex) {
                last = ex;
            }
        }
        throw new SQLException("Không thể kết nối CSDL p_documents với các mật khẩu đã cấu hình.", last);
    }

    protected void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {}
        }
    }

    protected void close(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ignored) {}
        }
    }

    protected void close(PreparedStatement ps, ResultSet rs) {
        close(rs);
        close(ps);
    }
}
