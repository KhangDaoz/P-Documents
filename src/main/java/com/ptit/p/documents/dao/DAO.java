package com.ptit.p.documents.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp DAO cơ sở — nơi duy nhất trong hệ thống mở kết nối tới CSDL.
 *
 * Chỉ có một thuộc tính {@code con} và một getter {@code getCon()}.
 * Mọi lớp DAO cụ thể kế thừa lớp này và dùng chung kết nối qua {@code con}.
 */
public class DAO {

    protected static Connection con;

    public DAO() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://localhost:3306/p_documents"
                        + "?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true";
                String user = "root";
                String[] passwords = { "1812", "123456" };
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
