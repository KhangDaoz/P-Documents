package com.ptit.p.documents.dao;

import com.ptit.p.documents.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp DAO cơ sở. Các DAO cụ thể (BorrowingStatDAO, BorrowedBookDAO,
 * StockStatDAO) kế thừa lớp này để dùng chung cơ chế truy cập CSDL
 * (theo spec §1.a: "Các lớp DAO đều kế thừa lớp DAO").
 */
public abstract class DAO {

    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    protected void close(ResultSet rs) {
        if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
    }

    protected void close(PreparedStatement ps) {
        if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
    }

    protected void close(PreparedStatement ps, ResultSet rs) {
        close(rs);
        close(ps);
    }
}
