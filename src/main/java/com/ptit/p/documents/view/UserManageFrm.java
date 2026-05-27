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
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBackground(new Color(220, 224, 230));
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 400));
        pnl.setBackground(new Color(220, 224, 230));
        pnlMain.add(pnl);

        // 1. Edit Account button (Top)
        btnEditUser = new JButton("Edit Account");
        btnEditUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnEditUser.setBackground(Color.WHITE);
        btnEditUser.setForeground(Color.BLACK);
        btnEditUser.setFocusPainted(false);
        btnEditUser.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnEditUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditUser.setBounds(190, 80, 220, 45);
        btnEditUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnEditUser.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnEditUser.setBackground(Color.WHITE);
            }
        });
        btnEditUser.addActionListener(this);
        pnl.add(btnEditUser);

        // 2. Create Account button (Middle)
        btnAddUser = new JButton("Create Account");
        btnAddUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAddUser.setBackground(Color.WHITE);
        btnAddUser.setForeground(Color.BLACK);
        btnAddUser.setFocusPainted(false);
        btnAddUser.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnAddUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddUser.setBounds(190, 145, 220, 45);
        btnAddUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnAddUser.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnAddUser.setBackground(Color.WHITE);
            }
        });
        btnAddUser.addActionListener(this);
        pnl.add(btnAddUser);

        // 3. Delete Account button (Bottom)
        btnDeleteUser = new JButton("Delete Account");
        btnDeleteUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnDeleteUser.setBackground(Color.WHITE);
        btnDeleteUser.setForeground(Color.BLACK);
        btnDeleteUser.setFocusPainted(false);
        btnDeleteUser.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnDeleteUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDeleteUser.setBounds(190, 210, 220, 45);
        btnDeleteUser.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnDeleteUser.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnDeleteUser.setBackground(Color.WHITE);
            }
        });
        btnDeleteUser.addActionListener(this);
        pnl.add(btnDeleteUser);

        // 4. Back button (Bottom Right)
        btnBack = new JButton("Back");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(Color.BLACK);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.setBounds(450, 310, 100, 40);
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnBack.setBackground(new Color(245, 247, 250));
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
