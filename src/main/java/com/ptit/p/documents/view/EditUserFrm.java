package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.UserDAO;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditUserFrm extends JFrame implements ActionListener {
    private final User user;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JTextField txtFullName;
    private final JTextField txtPhone;
    private final JTextField txtRole;
    private final JButton btnUpdate;
    private final JButton btnCancel;

    public EditUserFrm(User user) {
        super("EditUserFrm");
        this.user = user;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBackground(new Color(220, 224, 230));
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 420));
        pnl.setBackground(new Color(220, 224, 230));
        pnlMain.add(pnl);

        // Tiêu đề form (hiển thị phẳng đẹp, không giống nút bấm)
        JLabel lblHeader = new JLabel("Update User Information", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(50, 60, 70));
        lblHeader.setBounds(100, 30, 400, 35);
        pnl.add(lblHeader);

        // Grid panel cho bảng thông tin
        JPanel pnlGrid = new JPanel(new GridLayout(6, 2, 0, 0));
        pnlGrid.setBounds(100, 75, 400, 210);
        pnlGrid.setBorder(BorderFactory.createLineBorder(new Color(180, 185, 195), 1));

        // Dòng 1: MNV
        pnlGrid.add(createGridLabel("Employee ID", Color.WHITE, false));
        String mnvStr = "NV" + String.format("%03d", user.getId());
        pnlGrid.add(createGridLabel(mnvStr, new Color(225, 228, 233), false));

        // Dòng 2: Họ tên
        pnlGrid.add(createGridLabel("Full Name", Color.WHITE, false));
        txtFullName = createGridTextField(user.getFullName(), true);
        pnlGrid.add(txtFullName);

        // Dòng 3: Tên đăng nhập
        pnlGrid.add(createGridLabel("Username", Color.WHITE, false));
        txtUsername = createGridTextField(user.getUsername(), true);
        pnlGrid.add(txtUsername);

        // Dòng 4: Mật khẩu
        pnlGrid.add(createGridLabel("Password", Color.WHITE, false));
        txtPassword = new JPasswordField(user.getPassword());
        txtPassword.putClientProperty("PasswordField.showRevealButton", true);
        txtPassword.setHorizontalAlignment(JTextField.CENTER);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(Color.BLACK);
        // Đã xoá setBorder để FlatLaf tự động quản lý viền và hiển thị nút mắt
        pnlGrid.add(txtPassword);

        // Dòng 5: Số điện thoại
        pnlGrid.add(createGridLabel("Phone", Color.WHITE, false));
        txtPhone = createGridTextField(user.getPhone(), true);
        pnlGrid.add(txtPhone);

        // Dòng 6: Quyền hạn
        pnlGrid.add(createGridLabel("Role", Color.WHITE, false));
        txtRole = createGridTextField(user.getRole(), true);
        pnlGrid.add(txtRole);

        pnl.add(pnlGrid);

        // Nút Huỷ và Cập nhật
        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.setBounds(130, 310, 140, 35);
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnCancel.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnCancel.setBackground(Color.WHITE);
            }
        });
        btnCancel.addActionListener(this);
        pnl.add(btnCancel);

        btnUpdate = new JButton("Update");
        btnUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnUpdate.setBackground(Color.WHITE);
        btnUpdate.setForeground(Color.BLACK);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorder(BorderFactory.createLineBorder(new Color(150, 160, 175)));
        btnUpdate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnUpdate.setBounds(330, 310, 140, 35);
        btnUpdate.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnUpdate.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnUpdate.setBackground(Color.WHITE);
            }
        });
        btnUpdate.addActionListener(this);
        pnl.add(btnUpdate);
    }

    private JLabel createGridLabel(String text, Color background, boolean bold) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 13));
        label.setBorder(BorderFactory.createLineBorder(new Color(180, 185, 195), 1));
        return label;
    }

    private JTextField createGridTextField(String text, boolean editable) {
        JTextField tf = new JTextField(text);
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setOpaque(true);
        tf.setBackground(editable ? Color.WHITE : new Color(225, 228, 233));
        tf.setForeground(Color.BLACK);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Đã xoá setBorder tuỳ chỉnh để FlatLaf có thể vẽ nút mắt và giao diện mặc định
        tf.setEditable(editable);
        tf.setEnabled(editable);
        return tf;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUpdate) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());
            String fullName = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();
            String role = txtRole.getText().trim();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String roleLower = role.toLowerCase();
            if (!roleLower.equals("admin") && !roleLower.equals("manager") && !roleLower.equals("staff")) {
                JOptionPane.showMessageDialog(this, "Role must be admin, manager, or staff!", "Invalid Role", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gọi lớp User để cập nhật dữ liệu vào thực thể (thông qua các hàm set)
            user.setUsername(username);
            user.setPassword(password);
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setRole(role);

            // Gọi phương thức updateUser() của lớp UserDAO
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.updateUser(user);

            if (success) {
                // Hệ thống hiển thị thông báo thành công
                JOptionPane.showMessageDialog(this, "Account updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Admin click nút OK trên thông báo -> gọi lại lớp UserManageFrm
                this.dispose();
                UserManageFrm manageFrm = new UserManageFrm();
                manageFrm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists or account not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnCancel) {
            this.dispose();
            SearchUserFrm searchFrm = new SearchUserFrm(SearchUserFrm.Mode.EDIT);
            searchFrm.setVisible(true);
        }
    }
}
