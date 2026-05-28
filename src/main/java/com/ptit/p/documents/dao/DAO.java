package com.ptit.p.documents.dao;

import java.sql.Connection;

public class DAO {
    protected Connection con;

    public DAO() {
        String dbUrl = "jdbc:mysql://localhost:3306/p_documents?useSSL=false&serverTimezone=UTC";
        String dbClass = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(dbClass);
            con = java.sql.DriverManager.getConnection(dbUrl, "root", "08082005");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
