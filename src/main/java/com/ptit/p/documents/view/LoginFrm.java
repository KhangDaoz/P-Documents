package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.UserDAO;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrm extends JFrame implements ActionListener {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;

    public LoginFrm() {
        super("LoginFrm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 450);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(500, 380));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlMain.add(pnl);

        JLabel lblLogin = new JLabel("Login", JLabel.CENTER);
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogin.setForeground(new Color(30, 41, 59));
        lblLogin.setBounds(150, 50, 200, 40);
        pnl.add(lblLogin);

        JLabel lblUser = new JLabel("Username:", JLabel.RIGHT);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(new Color(71, 85, 105));
        lblUser.setBounds(60, 130, 90, 30);
        pnl.add(lblUser);

        txtUsername = new JTextField(15);
        txtUsername.setBackground(Color.WHITE);
        txtUsername.setForeground(Color.BLACK);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(160, 130, 280, 30);
        pnl.add(txtUsername);

        JLabel lblPass = new JLabel("Password:", JLabel.RIGHT);
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(new Color(71, 85, 105));
        lblPass.setBounds(60, 200, 90, 30);
        pnl.add(lblPass);

        txtPassword = new JPasswordField(15);
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true);
        txtPassword.putClientProperty("PasswordField.showRevealButton", true);
        txtPassword.putClientProperty("showRevealButton", true);
        txtPassword.putClientProperty("FlatLaf.style", "showRevealButton: true");
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(160, 200, 280, 30);
        pnl.add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(96, 165, 250));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(195, 290, 110, 35);
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(new Color(59, 130, 246));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(new Color(96, 165, 250));
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

        User loginUser = new User();
        loginUser.setUsername(username);
        loginUser.setPassword(password);

        UserDAO userDAO = new UserDAO();
        User loggedUser = userDAO.checkLogin(loginUser);

        if (loggedUser == null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String role = loggedUser.getRole() == null ? "" : loggedUser.getRole().trim().toLowerCase();
        if ("admin".equals(role)) {
            new AdminHomeFrm().setVisible(true);
            dispose();
        } else if ("librarian".equals(role) || "manager".equals(role)) {
            new ManagerHomeFrm(loggedUser).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản không có quyền truy cập phù hợp.", "Lỗi phân quyền", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatIntelliJLaf.setup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new LoginFrm().setVisible(true));
    }
}
