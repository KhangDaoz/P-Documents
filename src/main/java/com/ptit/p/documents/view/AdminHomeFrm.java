package com.ptit.p.documents.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminHomeFrm extends JFrame implements ActionListener {
    private final JButton btnManageUsers;
    private final JButton btnLogout;

    public AdminHomeFrm() {
        super("AdminHomeFrm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 330);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBackground(new Color(240, 242, 245));
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(500, 230));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlMain.add(pnl);

        JLabel lblHeader = new JLabel("Admin Dashboard", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(30, 41, 59));
        lblHeader.setBounds(50, 20, 400, 35);
        pnl.add(lblHeader);

        btnManageUsers = new JButton("Manage Accounts");
        btnManageUsers.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnManageUsers.setBackground(new Color(59, 130, 246));
        btnManageUsers.setForeground(Color.WHITE);
        btnManageUsers.setFocusPainted(false);
        btnManageUsers.setBorder(BorderFactory.createEmptyBorder());
        btnManageUsers.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnManageUsers.setBounds(140, 80, 220, 45);
        btnManageUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnManageUsers.setBackground(new Color(37, 99, 235));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnManageUsers.setBackground(new Color(59, 130, 246));
            }
        });
        btnManageUsers.addActionListener(this);
        pnl.add(btnManageUsers);

        btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(100, 116, 139));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setBounds(140, 145, 220, 45);
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogout.setBackground(new Color(248, 250, 252));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogout.setBackground(Color.WHITE);
            }
        });
        btnLogout.addActionListener(this);
        pnl.add(btnLogout); 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnManageUsers) {
            // Admin click chọn chức năng quản lý tài khoản.
            // Phương thức actionPerformed() gọi lớp UserManageFrm.
            this.dispose();
            UserManageFrm manageFrm = new UserManageFrm();
            manageFrm.setVisible(true);
        } else if (e.getSource() == btnLogout) {
            this.dispose();
            LoginFrm loginFrm = new LoginFrm();
            loginFrm.setVisible(true);
        }
    }
}
