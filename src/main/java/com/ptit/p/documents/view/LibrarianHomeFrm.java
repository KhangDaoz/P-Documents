package com.ptit.p.documents.view;

import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Giao diện chính của thủ thư sau khi đăng nhập.
 */
public class LibrarianHomeFrm extends JFrame implements ActionListener {

    private User    u;
    private JButton btnBookBorrow;
    private JButton btnCancelBorrow;

    public LibrarianHomeFrm(User u) {
        this.u = u;
        initComponents();
    }

    private void initComponents() {
        setTitle("He thong Quan ly Thu vien");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Thông tin người dùng
        String userInfo = (u != null)
                ? "Xin chao, " + u.getFullName() + " (" + u.getRole() + ")"
                : "He thong Quan ly Thu vien";
        JLabel lblUser = new JLabel(userInfo, SwingConstants.CENTER);
        main.add(lblUser, BorderLayout.NORTH);

        // Các nút chức năng
        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 8, 8));

        btnBookBorrow   = new JButton("Dat sach");
        btnCancelBorrow = new JButton("Huy dat sach");

        btnBookBorrow.addActionListener(this);
        btnCancelBorrow.addActionListener(this);

        btnPanel.add(btnBookBorrow);
        btnPanel.add(btnCancelBorrow);
        main.add(btnPanel, BorderLayout.CENTER);

        add(main);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBookBorrow) {
            new SearchBorrowFrm(u).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnCancelBorrow) {
            new SearchBorrowingFrm(u).setVisible(true);
            this.dispose();
        }
    }
}
