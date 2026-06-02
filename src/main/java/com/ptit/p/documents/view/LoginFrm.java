package com.ptit.p.documents.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.ptit.p.documents.dao.UserDAO;
import com.ptit.p.documents.model.User;

public class LoginFrm extends JFrame implements ActionListener {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;

    public LoginFrm() {
        super("Đăng nhập hệ thống");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(500, 380));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlMain.add(pnl);

        JLabel lblLogin = new JLabel("Đăng nhập", JLabel.CENTER);
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogin.setForeground(new Color(30, 41, 59));
        lblLogin.setBounds(150, 50, 200, 40);
        pnl.add(lblLogin);

        JLabel lblUser = new JLabel("Tên đăng nhập:", JLabel.RIGHT);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(new Color(71, 85, 105));
        lblUser.setBounds(10, 130, 140, 30);
        pnl.add(lblUser);

        txtUsername = new JTextField(15);
        txtUsername.setBackground(Color.WHITE);
        txtUsername.setForeground(Color.BLACK);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(160, 130, 280, 30);
        pnl.add(txtUsername);

        JLabel lblPass = new JLabel("Mật khẩu:", JLabel.RIGHT);
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(new Color(71, 85, 105));
        lblPass.setBounds(10, 200, 140, 30);
        pnl.add(lblPass);

        txtPassword = new JPasswordField(15);
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(160, 200, 280, 30);
        pnl.add(txtPassword);

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnLogin.setBackground(Color.WHITE);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(195, 290, 110, 35);
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(new Color(230, 235, 240));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(Color.WHITE);
            }
        });
        btnLogin.addActionListener(this);
        pnl.add(btnLogin);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != btnLogin) {
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User loggedUser = userDAO.checkLogin(username, password);

        if (loggedUser == null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String role = loggedUser.getRole() == null ? "" : loggedUser.getRole().trim().toLowerCase();
        if (role.equals("admin")) {
            new AdminHomeFrm(loggedUser).setVisible(true);
            dispose();
        } else if (role.equals("manager")) {
            new ManagerHomeFrm(loggedUser).setVisible(true);
            dispose();
        } else if (role.equals("librarian")) {
            new LibrarianHomeFrm(loggedUser).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản không có quyền truy cập phù hợp.", "Lỗi phân quyền", JOptionPane.ERROR_MESSAGE);
        }
    }
}
