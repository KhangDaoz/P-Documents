package com.ptit.p.documents.view;

import javax.swing.*;
import java.awt.*;

/**
 * Form đăng nhập stub (admin/admin).
 * Spec ghi rõ "bỏ qua phần đăng nhập" - form này chỉ là điểm vào tối thiểu,
 * không thay thế module xác thực thực sự.
 */
public class LoginFrm extends JFrame {

    public LoginFrm() {
        setTitle("Đăng nhập - Library Management System");
        setSize(380, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);

        g.gridx = 0; g.gridy = 0; center.add(new JLabel("Tên đăng nhập:"), g);
        g.gridx = 1; g.gridy = 0; center.add(txtUser, g);
        g.gridx = 0; g.gridy = 1; center.add(new JLabel("Mật khẩu:"), g);
        g.gridx = 1; g.gridy = 1; center.add(txtPass, g);

        JButton btnLogin = new JButton("Đăng nhập");
        g.gridx = 1; g.gridy = 2; center.add(btnLogin, g);

        JLabel hint = new JLabel("(admin / admin)", SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        add(hint, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> {
            String u = txtUser.getText().trim();
            String p = new String(txtPass.getPassword());
            if ("admin".equals(u) && "admin".equals(p)) {
                dispose();
                new ManagerHomeFrm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Sai tên đăng nhập hoặc mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        getRootPane().setDefaultButton(btnLogin);
    }
}
