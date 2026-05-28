package com.ptit.p.documents;

import com.ptit.p.documents.view.LoginFrm;
import javax.swing.SwingUtilities;

/**
 * Entry point cho hệ thống Quản lý Thư viện.
 * Khởi chạy giao diện đăng nhập LoginFrm.
 *
 * @author ADMIN
 */
public class PDocuments {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrm(null).setVisible(true);
        });
    }
}
