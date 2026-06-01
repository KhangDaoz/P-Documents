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
                String url = "jdbc:mysql://localhost:3306/p_documents?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true";
                String user = "root";
                String[] passwords = { "08082005", "123456","1812","hieun0l0ve"};
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
                    System.err.println("[DB] Không thể kết nối CSDL. DAO sẽ hoạt động ở chế độ không kết nối.");
                }
            }
        } catch (Exception e) {
            System.err.println("[DB] Không thể thiết lập kết nối CSDL trong DAO: " + e.getMessage());
        }
    }

    public Connection getCon() {
        return con;
    }
}
