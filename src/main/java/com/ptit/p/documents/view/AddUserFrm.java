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
        super("AddUserFrm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 460);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBackground(new Color(240, 242, 245));
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 390));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlMain.add(pnl);

        // Tiêu đề form (hiển thị phẳng đẹp, không giống nút bấm)
        JLabel lblHeader = new JLabel("Create New Account", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(30, 41, 59));
        lblHeader.setBounds(100, 30, 400, 35);
        pnl.add(lblHeader);

        // Grid panel cho bảng thông tin
        JPanel pnlGrid = new JPanel(new GridLayout(5, 2, 0, 0));
        pnlGrid.setBounds(100, 75, 400, 175);
        pnlGrid.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        // Dòng 1: Họ tên
        pnlGrid.add(createGridLabel("Full Name", Color.WHITE));
        txtFullName = createGridTextField();
        pnlGrid.add(txtFullName);

        // Dòng 2: Tên đăng nhập
        pnlGrid.add(createGridLabel("Username", Color.WHITE));
        txtUsername = createGridTextField();
        pnlGrid.add(txtUsername);

        // Dòng 3: Mật khẩu
        pnlGrid.add(createGridLabel("Password", Color.WHITE));
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true);
        txtPassword.putClientProperty("PasswordField.showRevealButton", true);
        txtPassword.putClientProperty("showRevealButton", true);
        txtPassword.putClientProperty("FlatLaf.style", "showRevealButton: true");
        txtPassword.setHorizontalAlignment(JTextField.CENTER);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(Color.BLACK);
        // Đã xoá setBorder để FlatLaf tự động quản lý viền và hiển thị nút mắt
        pnlGrid.add(txtPassword);

        // Dòng 4: Số điện thoại
        pnlGrid.add(createGridLabel("Phone", Color.WHITE));
        txtPhone = createGridTextField();
        pnlGrid.add(txtPhone);

        // Dòng 5: Quyền hạn
        pnlGrid.add(createGridLabel("Role", Color.WHITE));
        txtRole = createGridTextField();
        pnlGrid.add(txtRole);

        pnl.add(pnlGrid);

        // Nút Thêm mới và Huỷ
        btnAddnew = new JButton("Save");
        btnAddnew.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddnew.setBackground(new Color(59, 130, 246));
        btnAddnew.setForeground(Color.WHITE);
        btnAddnew.setFocusPainted(false);
        btnAddnew.setBorder(BorderFactory.createEmptyBorder());
        btnAddnew.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddnew.setBounds(130, 280, 140, 35);
        btnAddnew.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnAddnew.setBackground(new Color(37, 99, 235));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnAddnew.setBackground(new Color(59, 130, 246));
            }
        });
        btnAddnew.addActionListener(this);
        pnl.add(btnAddnew);

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(new Color(100, 116, 139));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.setBounds(330, 280, 140, 35);
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnCancel.setBackground(new Color(248, 250, 252));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnCancel.setBackground(Color.WHITE);
            }
        });
        btnCancel.addActionListener(this);
        pnl.add(btnCancel);
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
        // Đã xoá setBorder tuỳ chỉnh để FlatLaf có thể vẽ nút mắt và giao diện mặc định
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
                JOptionPane.showMessageDialog(this, "Please fill out all fields before saving!", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String roleLower = role.toLowerCase();
            if (!roleLower.equals("admin") && !roleLower.equals("manager") && !roleLower.equals("staff")) {
                JOptionPane.showMessageDialog(this, "Role must be admin, manager, or staff!", "Invalid Role", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gọi lớp User để thực hiện đóng gói dữ liệu. Các hàm set được gọi.
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setRole(role);

            // Phương thức actionPerformed() gọi lớp ConfirmAddUserFrm
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
