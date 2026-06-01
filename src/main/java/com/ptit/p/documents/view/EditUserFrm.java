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
        super("Sửa tài khoản");
        this.user = user;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 470);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 420));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlMain.add(pnl);

        
        JLabel lblHeader = new JLabel("Cập nhật thông tin tài khoản", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(30, 41, 59));
        lblHeader.setBounds(100, 30, 400, 35);
        pnl.add(lblHeader);

        
        JPanel pnlGrid = new JPanel(new GridLayout(6, 2, 0, 0));
        pnlGrid.setBounds(100, 75, 400, 210);
        pnlGrid.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        
        pnlGrid.add(createGridLabel("Mã nhân viên", Color.WHITE, false));
        String mnvStr = "NV" + String.format("%03d", user.getId());
        pnlGrid.add(createGridLabel(mnvStr, new Color(248, 250, 252), false));

        
        pnlGrid.add(createGridLabel("Họ và tên", Color.WHITE, false));
        txtFullName = createGridTextField(user.getFullName(), true);
        pnlGrid.add(txtFullName);

        
        pnlGrid.add(createGridLabel("Tên đăng nhập", Color.WHITE, false));
        txtUsername = createGridTextField(user.getUsername(), true);
        pnlGrid.add(txtUsername);

        
        pnlGrid.add(createGridLabel("Mật khẩu", Color.WHITE, false));
        txtPassword = new JPasswordField(user.getPassword());
        txtPassword.putClientProperty("JPasswordField.showRevealButton", true);
        txtPassword.putClientProperty("PasswordField.showRevealButton", true);
        txtPassword.putClientProperty("showRevealButton", true);
        txtPassword.putClientProperty("FlatLaf.style", "showRevealButton: true");
        txtPassword.setHorizontalAlignment(JTextField.CENTER);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(Color.BLACK);
        
        pnlGrid.add(txtPassword);

        
        pnlGrid.add(createGridLabel("Số điện thoại", Color.WHITE, false));
        txtPhone = createGridTextField(user.getPhone(), true);
        pnlGrid.add(txtPhone);

        
        pnlGrid.add(createGridLabel("Quyền hạn", Color.WHITE, false));
        txtRole = createGridTextField(user.getRole(), true);
        pnlGrid.add(txtRole);

        pnl.add(pnlGrid);

        
        btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(new Color(50, 60, 70));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.setBounds(130, 310, 140, 35);
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

        btnUpdate = new JButton("Cập nhật");
        btnUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnUpdate.setBackground(Color.WHITE);
        btnUpdate.setForeground(Color.BLACK);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnUpdate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnUpdate.setBounds(330, 310, 140, 35);
        btnUpdate.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnUpdate.setBackground(new Color(230, 235, 240));
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
        label.setForeground(new Color(71, 85, 105));
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 13));
        label.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        return label;
    }

    private JTextField createGridTextField(String text, boolean editable) {
        JTextField tf = new JTextField(text);
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setOpaque(true);
        tf.setBackground(editable ? Color.WHITE : new Color(248, 250, 252));
        tf.setForeground(Color.BLACK);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

            
            user.setUsername(username);
            user.setPassword(password);
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setRole(role);

            
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.updateUser(user);

            if (success) {
                
                JOptionPane.showMessageDialog(this, "Account updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
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
