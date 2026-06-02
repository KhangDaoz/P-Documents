package com.ptit.p.documents.view;

import com.ptit.p.documents.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminHomeFrm extends JFrame implements ActionListener {
    private final JButton btnManageUsers;
    private final JButton btnLogout;
    private final User user;

    public AdminHomeFrm(User user) {
        super("Trang quản trị - " + (user != null ? user.getFullName() : "Quản trị"));
        this.user = user;
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 330);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(500, 230));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlMain.add(pnl);

        JLabel lblHeader = new JLabel("Trang quản trị", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(30, 41, 59));
        lblHeader.setBounds(50, 20, 400, 35);
        pnl.add(lblHeader);

        btnManageUsers = new JButton("Quản lý tài khoản");
        btnManageUsers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnManageUsers.setBackground(Color.WHITE);
        btnManageUsers.setForeground(Color.BLACK);
        btnManageUsers.setFocusPainted(false);
        btnManageUsers.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnManageUsers.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnManageUsers.setBounds(140, 80, 220, 45);
        btnManageUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnManageUsers.setBackground(new Color(230, 235, 240));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnManageUsers.setBackground(Color.WHITE);
            }
        });
        btnManageUsers.addActionListener(this);
        pnl.add(btnManageUsers);

        btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(50, 60, 70));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setBounds(140, 145, 220, 45);
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogout.setBackground(new Color(230, 235, 240));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogout.setBackground(Color.WHITE);
            }
        });
        btnLogout.addActionListener(this);
        pnl.add(btnLogout); 

    }
    public AdminHomeFrm() {
        this(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnManageUsers) {
            // Admin click chọn chức năng quản lý tài khoản.
            this.dispose();
            UserManageFrm manageFrm = new UserManageFrm(this.user);
            manageFrm.setVisible(true);
        } else if (e.getSource() == btnLogout) {
            this.dispose();
            LoginFrm loginFrm = new LoginFrm();
            loginFrm.setVisible(true);
        }
    }
}
