package com.ptit.p.documents.view;

import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LibrarianHomeFrm extends JFrame {
    private User currentUser;

    private JButton btnConfirmBorrowing;
    private JButton btnReturnBook;
    private JButton btnLogout;

    public LibrarianHomeFrm(User currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }

    private void initComponents() {
        setTitle("Trang chủ - Thủ thư: " + currentUser.getFullName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // NORTH: pnlHeader
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblWelcome = new JLabel("Xin chào, " + currentUser.getFullName());
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JLabel lblRole = new JLabel("Vai trò: Thủ thư");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        pnlHeader.add(lblWelcome, BorderLayout.WEST);
        pnlHeader.add(lblRole, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // WEST: pnlMenu
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setPreferredSize(new Dimension(180, 0));
        pnlMenu.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnConfirmBorrowing = new JButton("XỬ LÝ NHẬN SÁCH");
        btnReturnBook = new JButton("TRẢ SÁCH");
        btnLogout = new JButton("Đăng xuất");

        // Style buttons
        Dimension btnSize = new Dimension(160, 40);
        btnConfirmBorrowing.setMaximumSize(btnSize);
        btnReturnBook.setMaximumSize(btnSize);
        btnLogout.setMaximumSize(btnSize);

        pnlMenu.add(btnConfirmBorrowing);
        pnlMenu.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlMenu.add(btnReturnBook);
        pnlMenu.add(Box.createVerticalGlue());
        pnlMenu.add(btnLogout);

        add(pnlMenu, BorderLayout.WEST);

        // CENTER: pnlContent
        JPanel pnlContent = new JPanel(new CardLayout());
        add(pnlContent, BorderLayout.CENTER);

        // SOUTH: pnlFooter
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblStatus = new JLabel("Sẵn sàng");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlFooter.add(lblStatus);
        add(pnlFooter, BorderLayout.SOUTH);

        // ACTIONS
        btnConfirmBorrowing.addActionListener(e -> {
            new SearchBorrowingFrm(currentUser, SearchMode.CONFIRM_BORROW).setVisible(true);
        });

        btnReturnBook.addActionListener(e -> {
            new SearchBorrowingFrm(currentUser, SearchMode.RETURN_BOOK).setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrm().setVisible(true);
        });
    }
}
