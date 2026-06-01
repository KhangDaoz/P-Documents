package com.ptit.p.documents.dao;

import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

public class ResetDBTest {

    @Test
    public void resetDatabase() throws Exception {
        System.out.println("--- Bắt đầu reset CSDL ---");
        DAO dao = new DAO();
        Connection con = dao.getCon();
        if (con == null) {
            System.err.println("Không thể kết nối database!");
            return;
        }

        // Đọc schema.sql
        String content = new String(Files.readAllBytes(Paths.get("src/main/resources/schema.sql")), "UTF-8");
        
        // Tách các câu lệnh SQL bằng dấu chấm phẩy
        // Lưu ý: Có thể có dấu chấm phẩy bên trong chuỗi hoặc chú thích, nhưng vì schema.sql đơn giản nên split bằng ";" là ổn.
        String[] statements = content.split(";");
        
        try (Statement stmt = con.createStatement()) {
            con.setAutoCommit(false);
            for (String sql : statements) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    try {
                        stmt.execute(trimmedSql);
                    } catch (Exception e) {
                        System.err.println("Lỗi khi chạy lệnh SQL: " + trimmedSql);
                        System.err.println("Chi tiết lỗi: " + e.getMessage());
                        throw e;
                    }
                }
            }
            con.commit();
            con.setAutoCommit(true);
            System.out.println("--- Reset CSDL thành công! ---");
        } catch (Exception e) {
            con.rollback();
            con.setAutoCommit(true);
            System.err.println("--- Reset CSDL thất bại! ---");
            throw e;
        }
    }
}
