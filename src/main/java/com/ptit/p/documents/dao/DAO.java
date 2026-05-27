package com.ptit.p.documents.dao;

import java.sql.Connection;

public class DAO {
    protected Connection con;

    public DAO() {
        // Giao tiếp DB thực tế sẽ được thiết lập tại đây
        // con = DriverManager.getConnection(url, username, password);
    }
}
