package com.ptit.p.documents.dao;

import com.ptit.p.documents.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp DAO cơ sở — quản lý kết nối CSDL dùng chung cho tất cả các lớp DAO.
 * Tất cả các lớp DAO cụ thể kế thừa lớp này và sử dụng chung DatabaseConnection singleton.
 */
public class DAO {
    protected Connection con;

    public DAO() {
        try {
            // Lấy connection tập trung từ DatabaseConnection singleton
            this.con = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Không thể thiết lập kết nối CSDL trong DAO", e);
        }
    }

    public Connection getConnection() {
        try {
            // Đảm bảo connection còn mở
            return DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            return this.con;
        }
    }

    public Connection getCon() {
        return getConnection();
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
