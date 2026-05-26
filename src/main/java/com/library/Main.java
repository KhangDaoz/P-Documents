package com.library;

import com.library.view.LoginFrm;

import javax.swing.*;

/**
 * Điểm vào ứng dụng Library Management System.
 *
 * Spec CNPM.md ghi "bỏ qua phần đăng nhập" (đã thiết kế ở chức năng khác).
 * Ở đây dùng LoginFrm stub (admin/admin) để có điểm vào, sau đó mở ManagerHomeFrm
 * - đúng điểm bắt đầu mà spec §1.b bước 1 mô tả.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrm().setVisible(true));
    }
}
