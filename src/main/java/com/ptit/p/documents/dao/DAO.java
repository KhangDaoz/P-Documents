package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
    private Connection connection;

    public DAO() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/p_documents", "root", "123456");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish DB connection", e);
        }
    }

    protected Connection getConnection() {
        return this.connection;
    }
}
