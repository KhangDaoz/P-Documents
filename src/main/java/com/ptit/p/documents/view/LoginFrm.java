package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.UserDAO;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrm extends JFrame {
    public static final String ROLE_LIBRARIAN = "librarian";
    public static final String ROLE_ADMIN = "admin";

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    
    private UserDAO userDAO;

    public LoginFrm() {
        userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng nhập hệ thống");
        setSize(400, 280);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // NORTH: pnlHeader
        JPanel pnlHeader = new JPanel();
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // CENTER: pnlForm
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        txtUsername = new JTextField(20);
        
        JLabel lblPassword = new JLabel("Mật khẩu:");
        txtPassword = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(lblUsername, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        pnlForm.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(lblPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        pnlForm.add(txtPassword, gbc);
        
        add(pnlForm, BorderLayout.CENTER);

        // SOUTH: pnlButtons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnLogin = new JButton("Đăng nhập");
        btnCancel = new JButton("Thoát");
        
        pnlButtons.add(btnLogin);
        pnlButtons.add(btnCancel);
        add(pnlButtons, BorderLayout.SOUTH);

        // ACTIONS
        btnLogin.addActionListener(e -> loginAction());
        btnCancel.addActionListener(e -> System.exit(0));
    }

    private void loginAction() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.checkLogin(username, password);
        if (user != null) {
            String role = user.getRole();
            if (ROLE_LIBRARIAN.equals(role)) {
                new LibrarianHomeFrm(user).setVisible(true);
            } else if (ROLE_ADMIN.equals(role)) {
                new AdminHomeFrm(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không nhận diện được vai trò!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
