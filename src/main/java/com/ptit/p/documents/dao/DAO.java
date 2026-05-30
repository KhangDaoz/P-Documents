package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
    protected Connection con;

    public DAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/p_documents?useSSL=false&serverTimezone=UTC",
                    "root",
                    "123456");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish DB connection", e);
        }
    }

    protected Connection getConnection() {
        return this.con;
    }
}
