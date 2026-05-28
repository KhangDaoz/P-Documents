/*
 * Hệ thống Quản lý Thư viện — PTIT
 * Entry point của ứng dụng.
 */

package com.ptit.p.documents;

import com.ptit.p.documents.dao.DatabaseInitializer;
import com.ptit.p.documents.model.User;
import com.ptit.p.documents.view.LibrarianHomeFrm;

import javax.swing.*;

/**
 * Lớp khởi chạy ứng dụng Quản lý Thư viện.
 *
 * Để chạy trong NetBeans: Right-click project → Run (hoặc F6).
 * Đảm bảo MySQL đang chạy — database p_documents sẽ tự động được tạo.
 */
public class PDocuments {

    public static void main(String[] args) {
        // Dùng Look & Feel của hệ điều hành để giao diện đẹp hơn
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // ---- Khởi tạo database tự động ----
        boolean dbReady = DatabaseInitializer.init();
        if (!dbReady) {
            JOptionPane.showMessageDialog(null,
                    "Không thể kết nối hoặc khởi tạo database!\n"
                    + "Vui lòng kiểm tra:\n"
                    + "  • MySQL đang chạy trên localhost:3306\n"
                    + "  • Username/password trong DatabaseInitializer đúng",
                    "Lỗi kết nối Database",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Tạo user mẫu (thay bằng màn hình Login thực tế sau)
        User testUser = new User(1, "librarian1", "Trần Thị Thư", "librarian");

        // Khởi chạy trên Event Dispatch Thread (EDT) — bắt buộc với Swing
        SwingUtilities.invokeLater(() -> {
            new LibrarianHomeFrm(testUser).setVisible(true);
        });
    }
}

