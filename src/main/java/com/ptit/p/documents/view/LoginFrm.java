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
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Panel nền sử dụng GridBagLayout để căn giữa
        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBackground(new Color(220, 224, 230));
        setContentPane(pnlMain);

        // Card panel chứa nội dung form với vị trí tuyệt đối như thiết kế
        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(500, 380));
        pnl.setBackground(new Color(220, 224, 230));
        pnlMain.add(pnl);

        JLabel lblLogin = new JLabel("Login", JLabel.CENTER);
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogin.setForeground(new Color(50, 60, 70));
        lblLogin.setBounds(150, 50, 200, 40);
        pnl.add(lblLogin);

        JLabel lblUser = new JLabel("Username:", JLabel.RIGHT);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(Color.BLACK);
        lblUser.setBounds(60, 130, 90, 30);
        pnl.add(lblUser);

        txtUsername = new JTextField(15);
        txtUsername.setBackground(Color.WHITE);
        txtUsername.setForeground(Color.BLACK);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Đã xoá setBorder để FlatLaf vẽ giao diện mặc định đẹp hơn
        txtUsername.setBounds(160, 130, 280, 30);
        pnl.add(txtUsername);

        JLabel lblPass = new JLabel("Password:", JLabel.RIGHT);
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(Color.BLACK);
        lblPass.setBounds(60, 200, 90, 30);
        pnl.add(lblPass);

        txtPassword = new JPasswordField(15);
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Đã xoá setBorder để FlatLaf tự động quản lý viền và hiển thị nút mắt
        txtPassword.setBounds(160, 200, 280, 30);
        pnl.add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnLogin.setBackground(Color.WHITE);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(195, 290, 110, 35);
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(new Color(245, 247, 250));
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
        if (e.getSource() == btnLogin) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Gọi lớp User để đóng gói thông tin đăng nhập
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            // Gọi phương thức checkLogin() của lớp UserDAO
            UserDAO userDAO = new UserDAO();
            User loggedUser = userDAO.checkLogin(user);

            if (loggedUser != null) {
                if ("admin".equalsIgnoreCase(loggedUser.getRole())) {
                    // Nếu là Admin, ẩn form đăng nhập và mở AdminHomeFrm
                    this.dispose();
                    AdminHomeFrm homeFrm = new AdminHomeFrm();
                    homeFrm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Your account does not have Admin access privileges!", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect username or password!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatIntelliJLaf.setup();
            System.out.println("DEBUG: Active LookAndFeel is -> " + UIManager.getLookAndFeel().getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new LoginFrm().setVisible(true);
        });
    }
}
