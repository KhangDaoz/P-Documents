/*
 * Hệ thống Quản lý Thư viện — PTIT
 * Entry point của ứng dụng.
 */

package com.ptit.p.documents;

import com.ptit.p.documents.dao.DatabaseInitializer;
import com.ptit.p.documents.view.LoginFrm;

import javax.swing.*;

/**
 * Lớp khởi chạy ứng dụng Quản lý Thư viện.
 */
public class PDocuments {

    public static void main(String[] args) {
        // Dùng FlatLaf để giao diện đẹp hơn
        try {
            boolean success = com.formdev.flatlaf.FlatIntelliJLaf.setup();
            System.out.println("DEBUG: FlatLaf setup success? " + success);
            UIManager.put("PasswordField.showRevealButton", true);
            UIManager.put("JPasswordField.showRevealButton", true);
            System.out.println("DEBUG: Active LookAndFeel is -> " + UIManager.getLookAndFeel().getName());
        } catch (Exception ex) {
            System.err.println("FlatLaf Look and Feel setup failed: " + ex.getMessage());
        }

        // ---- Khởi tạo database tự động ----
        boolean dbReady = DatabaseInitializer.init();
        if (!dbReady) {
            JOptionPane.showMessageDialog(null,
                    "Không thể kết nối hoặc khởi tạo database!\n"
                    + "Vui lòng kiểm tra:\n"
                    + "  • MySQL đang chạy trên localhost:3306\n"
                    + "  • Username/password của root đã được thử (1812 và 123456)",
                    "Lỗi kết nối Database",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Khởi chạy trên Event Dispatch Thread (EDT) — bắt buộc với Swing
        SwingUtilities.invokeLater(() -> {
            new LoginFrm().setVisible(true);
        });
    }
}
