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
        setSize(660, 420);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBackground(new Color(220, 224, 230));
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 350));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(190, 195, 205), 1));
        pnlMain.add(pnl);

        JLabel lblHeader = new JLabel("Admin Dashboard", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(50, 60, 70));
        lblHeader.setBounds(100, 30, 400, 35);
        pnl.add(lblHeader);

        btnManageUsers = new JButton("Manage Accounts");
        btnManageUsers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnManageUsers.setBackground(Color.WHITE);
        btnManageUsers.setForeground(Color.BLACK);
        btnManageUsers.setFocusPainted(false);
        btnManageUsers.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnManageUsers.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnManageUsers.setBounds(190, 90, 220, 50);
        btnManageUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnManageUsers.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnManageUsers.setBackground(Color.WHITE);
            }
        });
        btnManageUsers.addActionListener(this);
        pnl.add(btnManageUsers);

        JButton btnStats = new JButton("Statistics");
        btnStats.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnStats.setBackground(Color.WHITE);
        btnStats.setForeground(Color.BLACK);
        btnStats.setFocusPainted(false);
        btnStats.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnStats.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnStats.setBounds(190, 160, 220, 50);
        btnStats.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnStats.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnStats.setBackground(Color.WHITE);
            }
        });
        pnl.add(btnStats);

        // Keep logout reference if needed by event handlers, though it's not visible on home screenshot
        btnLogout = new JButton(); 
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
