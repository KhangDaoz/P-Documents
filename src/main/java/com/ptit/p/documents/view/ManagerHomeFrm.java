package com.ptit.p.documents.view;

import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerHomeFrm extends JFrame implements ActionListener {
    private JButton btnBookManage;
    private JButton btnLogout;
    private User currentUser;

    public ManagerHomeFrm(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Trang chủ quản lý - " + currentUser.getFullName());
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("QUẢN LÝ THƯ VIỆN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        // // Welcome
        // JLabel lblWelcome = new JLabel("Xin chào, " + currentUser.getFullName() + " (" + currentUser.getRole() + ")",
        //         SwingConstants.CENTER);
        // lblWelcome.setFont(new Font("Arial", Font.PLAIN, 14));
        // gbc.gridy = 1;
        // mainPanel.add(lblWelcome, gbc);

        // // Separator
        // gbc.gridy = 2;
        // mainPanel.add(new JSeparator(), gbc);

        // Book Management button
        btnBookManage = new JButton("Quản lý thông tin sách");
        btnBookManage.setFont(new Font("Arial", Font.BOLD, 14));
        btnBookManage.setPreferredSize(new Dimension(250, 50));
        gbc.gridy = 3;
        mainPanel.add(btnBookManage, gbc);

        // Logout button
        btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Arial", Font.PLAIN, 12));
        btnLogout.setPreferredSize(new Dimension(250, 35));
        gbc.gridy = 4;
        mainPanel.add(btnLogout, gbc);

        btnBookManage.addActionListener(this);
        btnLogout.addActionListener(this);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBookManage) {
            new BookManageFrm(currentUser).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrm().setVisible(true);
                this.dispose();
            }
        }
    }
}
