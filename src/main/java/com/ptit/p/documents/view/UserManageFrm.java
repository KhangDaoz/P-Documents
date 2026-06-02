package com.ptit.p.documents.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.ptit.p.documents.model.User;

public class UserManageFrm extends JFrame implements ActionListener {
    private final JButton btnAddUser;
    private final JButton btnEditUser;
    private final JButton btnDeleteUser;
    private final JButton btnBack;
    private final User user;

    public UserManageFrm(User user) {
        super("Quản lý tài khoản");
        this.user = user;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 470);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 400));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlMain.add(pnl);

        JLabel lblHeader = new JLabel("Quản lý tài khoản", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(30, 41, 59));
        lblHeader.setBounds(100, 25, 400, 35);
        pnl.add(lblHeader);
        btnEditUser = new JButton("Sửa tài khoản");
        btnEditUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnEditUser.setBackground(Color.WHITE);
        btnEditUser.setForeground(Color.BLACK);
        btnEditUser.setFocusPainted(false);
        btnEditUser.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnEditUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditUser.setBounds(190, 80, 220, 45);
        btnEditUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnEditUser.setBackground(new Color(230, 235, 240));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnEditUser.setBackground(Color.WHITE);
            }
        });
        btnEditUser.addActionListener(this);
        pnl.add(btnEditUser);
        btnAddUser = new JButton("Tạo tài khoản");
        btnAddUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAddUser.setBackground(Color.WHITE);
        btnAddUser.setForeground(Color.BLACK);
        btnAddUser.setFocusPainted(false);
        btnAddUser.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnAddUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddUser.setBounds(190, 145, 220, 45);
        btnAddUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnAddUser.setBackground(new Color(230, 235, 240));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnAddUser.setBackground(Color.WHITE);
            }
        });
        btnAddUser.addActionListener(this);
        pnl.add(btnAddUser);
        btnDeleteUser = new JButton("Xóa tài khoản");
        btnDeleteUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnDeleteUser.setBackground(Color.WHITE);
        btnDeleteUser.setForeground(Color.BLACK);
        btnDeleteUser.setFocusPainted(false);
        btnDeleteUser.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnDeleteUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDeleteUser.setBounds(190, 210, 220, 45);
        btnDeleteUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnDeleteUser.setBackground(new Color(230, 235, 240));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnDeleteUser.setBackground(Color.WHITE);
            }
        });
        btnDeleteUser.addActionListener(this);
        pnl.add(btnDeleteUser);
        btnBack = new JButton("Trở về");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(new Color(50, 60, 70));
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setBounds(450, 310, 100, 40);
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnBack.setBackground(new Color(230, 235, 240));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnBack.setBackground(Color.WHITE);
            }
        });
        btnBack.addActionListener(this);
        pnl.add(btnBack);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddUser) {
            this.dispose();
            AddUserFrm addFrm = new AddUserFrm(this.user);
            addFrm.setVisible(true);
        } else if (e.getSource() == btnEditUser) {
            this.dispose();
            SearchUserFrm searchFrm = new SearchUserFrm(this.user);
            searchFrm.setVisible(true);
        } else if (e.getSource() == btnDeleteUser) {
            this.dispose();
            SearchUserFrm searchFrm = new SearchUserFrm(this.user);
            searchFrm.setVisible(true);
        } else if (e.getSource() == btnBack) {
            this.dispose();
            AdminHomeFrm homeFrm = new AdminHomeFrm(this.user);
            homeFrm.setVisible(true);
        }
    }
}
