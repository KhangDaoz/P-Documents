package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {

    protected static Connection con;

    public DAO() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql:
                        + "?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true";
                String user = "root";
                String[] passwords = { "08082005", "123456"};
                SQLException last = null;
                for (String pwd : passwords) {
                    try {
                        con = DriverManager.getConnection(url, user, pwd);
                        System.out.println("[DB] Kết nối CSDL p_documents thành công.");
                        break;
                    } catch (SQLException ex) {
                        last = ex;
                    }
                }
                if (con == null || con.isClosed()) {
                    throw new SQLException("Không thể kết nối CSDL.", last);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể thiết lập kết nối CSDL trong DAO", e);
        }
    }

    public Connection getCon() {
        return con;
    }
}
