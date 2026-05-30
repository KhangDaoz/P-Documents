package com.ptit.p.documents.view;

import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện chính của thủ thư sau khi đăng nhập.
 */
public class LibrarianHomeFrm extends JFrame {
    private User currentUser;

    private JButton btnBookBorrow;
    private JButton btnCancelBorrow;
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
        
        JLabel lblRole = new JLabel("Vai trò: " + currentUser.getRole());
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        pnlHeader.add(lblWelcome, BorderLayout.WEST);
        pnlHeader.add(lblRole, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // WEST: pnlMenu
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setPreferredSize(new Dimension(200, 0));
        pnlMenu.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnBookBorrow = new JButton("ĐẶT SÁCH");
        btnCancelBorrow = new JButton("HỦY ĐẶT SÁCH");
        btnConfirmBorrowing = new JButton("XỬ LÝ NHẬN SÁCH");
        btnReturnBook = new JButton("TRẢ SÁCH");
        btnLogout = new JButton("Đăng xuất");

        // Style buttons
        Dimension btnSize = new Dimension(180, 40);
        btnBookBorrow.setMaximumSize(btnSize);
        btnCancelBorrow.setMaximumSize(btnSize);
        btnConfirmBorrowing.setMaximumSize(btnSize);
        btnReturnBook.setMaximumSize(btnSize);
        btnLogout.setMaximumSize(btnSize);

        pnlMenu.add(btnBookBorrow);
        pnlMenu.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlMenu.add(btnCancelBorrow);
        pnlMenu.add(Box.createRigidArea(new Dimension(0, 15)));
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
        btnBookBorrow.addActionListener(e -> {
            new SearchBorrowFrm(currentUser).setVisible(true);
            this.dispose();
        });

        btnCancelBorrow.addActionListener(e -> {
            new SearchBorrowingFrm(currentUser, SearchMode.CANCEL_BORROW).setVisible(true);
            this.dispose();
        });

        btnConfirmBorrowing.addActionListener(e -> {
            new SearchBorrowingFrm(currentUser, SearchMode.CONFIRM_BORROW).setVisible(true);
            this.dispose();
        });

        btnReturnBook.addActionListener(e -> {
            new SearchBorrowingFrm(currentUser, SearchMode.RETURN_BOOK).setVisible(true);
            this.dispose();
        });

        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginFrm().setVisible(true);
        });
    }
}
