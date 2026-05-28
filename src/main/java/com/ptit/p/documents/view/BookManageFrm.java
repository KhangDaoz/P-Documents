package com.ptit.p.documents.view;

import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookManageFrm extends JFrame implements ActionListener {
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnBack;
    private User currentUser;

    public BookManageFrm(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Quản lý thông tin sách");
        setSize(450, 350);
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
        JLabel lblTitle = new JLabel("QUẢN LÝ THÔNG TIN SÁCH", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(lblTitle, gbc);

        // Separator
        gbc.gridy = 1;
        mainPanel.add(new JSeparator(), gbc);

        // Add Book button
        btnAdd = new JButton("Thêm thông tin sách");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(250, 45));
        btnAdd.setBackground(UIManager.getColor("Button.background"));
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 2;
        mainPanel.add(btnAdd, gbc);

        // Edit Book button
        btnEdit = new JButton("Sửa thông tin sách");
        btnEdit.setFont(new Font("Arial", Font.BOLD, 14));
        btnEdit.setPreferredSize(new Dimension(250, 45));
        btnEdit.setBackground(UIManager.getColor("Button.background"));
        btnEdit.setForeground(Color.BLACK);
        btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 3;
        mainPanel.add(btnEdit, gbc);

        // Delete Book button
        btnDelete = new JButton("Xóa thông tin sách");
        btnDelete.setFont(new Font("Arial", Font.BOLD, 14));
        btnDelete.setPreferredSize(new Dimension(250, 45));
        btnDelete.setBackground(UIManager.getColor("Button.background"));
        btnDelete.setForeground(Color.BLACK);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        mainPanel.add(btnDelete, gbc);

        // Back button
        btnBack = new JButton("Quay lại trang chủ");
        btnBack.setFont(new Font("Arial", Font.PLAIN, 12));
        btnBack.setPreferredSize(new Dimension(250, 35));
        btnBack.setBackground(UIManager.getColor("Button.background"));
        btnBack.setForeground(Color.BLACK);
        gbc.gridy = 5;
        mainPanel.add(btnBack, gbc);

        btnAdd.addActionListener(this);
        btnEdit.addActionListener(this);
        btnDelete.addActionListener(this);
        btnBack.addActionListener(this);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            new AddBookFrm(currentUser).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnEdit) {
            new SearchBookFrm(currentUser, "edit").setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnDelete) {
            new SearchBookFrm(currentUser, "delete").setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnBack) {
            new ManagerHomeFrm(currentUser).setVisible(true);
            this.dispose();
        }
    }
}
