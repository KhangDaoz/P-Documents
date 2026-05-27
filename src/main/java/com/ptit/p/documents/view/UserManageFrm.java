package com.ptit.p.documents.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserManageFrm extends JFrame implements ActionListener {
    private final JButton btnAddUser;
    private final JButton btnEditUser;
    private final JButton btnDeleteUser;
    private final JButton btnBack;

    public UserManageFrm() {
        super("UserManageFrm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 470);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBackground(new Color(240, 242, 245));
        setContentPane(pnlMain);

        // Card panel chứa nội dung form với vị trí tuyệt đối như thiết kế
        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 400));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlMain.add(pnl);

        JLabel lblHeader = new JLabel("Manage Accounts", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(30, 41, 59));
        lblHeader.setBounds(100, 25, 400, 35);
        pnl.add(lblHeader);

        // 1. Edit Account button (Top) - Primary style
        btnEditUser = new JButton("Edit Account");
        btnEditUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEditUser.setBackground(new Color(59, 130, 246));
        btnEditUser.setForeground(Color.WHITE);
        btnEditUser.setFocusPainted(false);
        btnEditUser.setBorder(BorderFactory.createEmptyBorder());
        btnEditUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditUser.setBounds(190, 80, 220, 45);
        btnEditUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnEditUser.setBackground(new Color(37, 99, 235));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnEditUser.setBackground(new Color(59, 130, 246));
            }
        });
        btnEditUser.addActionListener(this);
        pnl.add(btnEditUser);

        // 2. Create Account button (Middle) - Primary style
        btnAddUser = new JButton("Create Account");
        btnAddUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddUser.setBackground(new Color(59, 130, 246));
        btnAddUser.setForeground(Color.WHITE);
        btnAddUser.setFocusPainted(false);
        btnAddUser.setBorder(BorderFactory.createEmptyBorder());
        btnAddUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddUser.setBounds(190, 145, 220, 45);
        btnAddUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnAddUser.setBackground(new Color(37, 99, 235));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnAddUser.setBackground(new Color(59, 130, 246));
            }
        });
        btnAddUser.addActionListener(this);
        pnl.add(btnAddUser);

        // 3. Delete Account button (Bottom) - Primary style
        btnDeleteUser = new JButton("Delete Account");
        btnDeleteUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDeleteUser.setBackground(new Color(59, 130, 246));
        btnDeleteUser.setForeground(Color.WHITE);
        btnDeleteUser.setFocusPainted(false);
        btnDeleteUser.setBorder(BorderFactory.createEmptyBorder());
        btnDeleteUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDeleteUser.setBounds(190, 210, 220, 45);
        btnDeleteUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnDeleteUser.setBackground(new Color(37, 99, 235));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnDeleteUser.setBackground(new Color(59, 130, 246));
            }
        });
        btnDeleteUser.addActionListener(this);
        pnl.add(btnDeleteUser);

        // 4. Back button (Bottom Right) - Secondary style
        btnBack = new JButton("Back");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(new Color(100, 116, 139));
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setBounds(450, 310, 100, 40);
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnBack.setBackground(new Color(248, 250, 252));
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
            AddUserFrm addFrm = new AddUserFrm();
            addFrm.setVisible(true);
        } else if (e.getSource() == btnEditUser) {
            this.dispose();
            SearchUserFrm searchFrm = new SearchUserFrm(SearchUserFrm.Mode.EDIT);
            searchFrm.setVisible(true);
        } else if (e.getSource() == btnDeleteUser) {
            this.dispose();
            SearchUserFrm searchFrm = new SearchUserFrm(SearchUserFrm.Mode.DELETE);
            searchFrm.setVisible(true);
        } else if (e.getSource() == btnBack) {
            this.dispose();
            AdminHomeFrm homeFrm = new AdminHomeFrm();
            homeFrm.setVisible(true);
        }
    }
}
