package com.ptit.p.documents.view;

import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddUserFrm extends JFrame implements ActionListener {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JTextField txtFullName;
    private final JTextField txtPhone;
    private final JTextField txtRole;
    private final JButton btnAddnew;
    private final JButton btnCancel;

    public AddUserFrm() {
        super("Thêm tài khoản");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 460);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 390));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlMain.add(pnl);

        JLabel lblHeader = new JLabel("Tạo tài khoản mới", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(30, 41, 59));
        lblHeader.setBounds(100, 30, 400, 35);
        pnl.add(lblHeader);

        
        JPanel pnlGrid = new JPanel(new GridLayout(5, 2, 0, 0));
        pnlGrid.setBounds(100, 75, 400, 175);
        pnlGrid.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        
        pnlGrid.add(createGridLabel("Họ và tên", Color.WHITE));
        txtFullName = createGridTextField();
        pnlGrid.add(txtFullName);

        
        pnlGrid.add(createGridLabel("Tên đăng nhập", Color.WHITE));
        txtUsername = createGridTextField();
        pnlGrid.add(txtUsername);

        
        pnlGrid.add(createGridLabel("Mật khẩu", Color.WHITE));
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true);
        txtPassword.putClientProperty("PasswordField.showRevealButton", true);
        txtPassword.putClientProperty("showRevealButton", true);
        txtPassword.putClientProperty("FlatLaf.style", "showRevealButton: true");
        txtPassword.setHorizontalAlignment(JTextField.CENTER);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(Color.BLACK);
        pnlGrid.add(txtPassword);

        
        pnlGrid.add(createGridLabel("Số điện thoại", Color.WHITE));
        txtPhone = createGridTextField();
        pnlGrid.add(txtPhone);

        
        pnlGrid.add(createGridLabel("Quyền hạn", Color.WHITE));
        txtRole = createGridTextField();
        pnlGrid.add(txtRole);

        pnl.add(pnlGrid);

        
        btnAddnew = new JButton("Thêm mới");
        btnAddnew.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAddnew.setBackground(Color.WHITE);
        btnAddnew.setForeground(Color.BLACK);
        btnAddnew.setFocusPainted(false);
        btnAddnew.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnAddnew.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddnew.setBounds(130, 280, 140, 35);
        btnAddnew.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnAddnew.setBackground(new Color(230, 235, 240));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnAddnew.setBackground(Color.WHITE);
            }
        });
        btnAddnew.addActionListener(this);
        pnl.add(btnAddnew);

        btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(new Color(50, 60, 70));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.setBounds(330, 280, 140, 35);
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnCancel.setBackground(new Color(230, 235, 240));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnCancel.setBackground(Color.WHITE);
            }
        });
        btnCancel.addActionListener(this);
        pnl.add(btnCancel);

    }

    
    public AddUserFrm(User user) {
        this();
        if (user != null) {
            txtFullName.setText(user.getFullName());
            txtUsername.setText(user.getUsername());
            txtPassword.setText(user.getPassword());
            txtPhone.setText(user.getPhone());
            txtRole.setText(user.getRole());
        }
    }

    private JLabel createGridLabel(String text, Color background) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(new Color(71, 85, 105));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        return label;
    }

    private JTextField createGridTextField() {
        JTextField tf = new JTextField();
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setOpaque(true);
        tf.setBackground(Color.WHITE);
        tf.setForeground(Color.BLACK);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return tf;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddnew) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String fullName = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();
            String role = txtRole.getText().trim();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (username.length() < 5 || username.length() > 20) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập phải từ 5 đến 20 ký tự!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (password.length() < 6 || password.length() > 32) {
                JOptionPane.showMessageDialog(this, "Mật khẩu phải từ 6 đến 32 ký tự!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải bao gồm đúng 10 chữ số!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String roleLower = role.toLowerCase();
            if (!roleLower.equals("admin") && !roleLower.equals("manager") && !roleLower.equals("librarian")) {
                JOptionPane.showMessageDialog(this, "Role must be admin, manager, or librarian!", "Invalid Role", JOptionPane.ERROR_MESSAGE);
                return;
            }

            
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setRole(role);

            
            this.dispose();
            ConfirmAddUserFrm confirmFrm = new ConfirmAddUserFrm(user);
            confirmFrm.setVisible(true);
        } else if (e.getSource() == btnCancel) {
            this.dispose();
            UserManageFrm manageFrm = new UserManageFrm();
            manageFrm.setVisible(true);
        }
    }
}
