package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.UserDAO;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmAddUserFrm extends JFrame implements ActionListener {
    private final User user;
    private final JTable tblAddUserConfirm;
    private final JButton btnConfirm;
    private final JButton btnCancel;

    public ConfirmAddUserFrm(User user) {
        super("ConfirmAddUserFrm");
        this.user = user;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBackground(new Color(220, 224, 230));
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 390));
        pnl.setBackground(new Color(220, 224, 230));
        pnlMain.add(pnl);

        // Tiêu đề form (hiển thị phẳng đẹp, không giống nút bấm)
        JLabel lblHeader = new JLabel("Confirm New Account Details", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(50, 60, 70));
        lblHeader.setBounds(100, 30, 400, 35);
        pnl.add(lblHeader);

        // Grid panel cho bảng thông tin
        JPanel pnlGrid = new JPanel(new GridLayout(5, 2, 0, 0));
        pnlGrid.setBounds(100, 75, 400, 175);
        pnlGrid.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Dòng 1: Họ tên
        pnlGrid.add(createGridLabel("Full Name", Color.WHITE));
        pnlGrid.add(createGridLabel(user.getFullName(), Color.WHITE));

        // Dòng 2: Tên đăng nhập
        pnlGrid.add(createGridLabel("Username", Color.WHITE));
        pnlGrid.add(createGridLabel(user.getUsername(), Color.WHITE));

        // Dòng 3: Mật khẩu
        pnlGrid.add(createGridLabel("Password", Color.WHITE));
        pnlGrid.add(createGridLabel("********", Color.WHITE));

        // Dòng 4: Số điện thoại
        pnlGrid.add(createGridLabel("Phone", Color.WHITE));
        pnlGrid.add(createGridLabel(user.getPhone(), Color.WHITE));

        // Dòng 5: Quyền hạn
        pnlGrid.add(createGridLabel("Role", Color.WHITE));
        String roleText = user.getRole();
        if ("admin".equalsIgnoreCase(roleText)) {
            roleText = "Admin";
        } else if ("manager".equalsIgnoreCase(roleText)) {
            roleText = "Manager";
        } else if ("staff".equalsIgnoreCase(roleText)) {
            roleText = "Staff";
        }
        pnlGrid.add(createGridLabel(roleText, Color.WHITE));

        pnl.add(pnlGrid);

        // Nút Xác nhận & Lưu ở góc dưới bên phải
        btnConfirm = new JButton("Confirm & Save");
        btnConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnConfirm.setBackground(Color.WHITE);
        btnConfirm.setForeground(Color.BLACK);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        btnConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirm.setBounds(350, 280, 150, 35);
        btnConfirm.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnConfirm.setBackground(new Color(245, 247, 250));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnConfirm.setBackground(Color.WHITE);
            }
        });
        btnConfirm.addActionListener(this);
        pnl.add(btnConfirm);

        // Cancel button is not visible on the screenshot, but keep variable to prevent compilation errors
        btnCancel = new JButton();
        
        // JTable is not used anymore in visual, keep dummy to satisfy other references
        tblAddUserConfirm = new JTable();
    }

    private JLabel createGridLabel(String text, Color background) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) {
            // Gọi lớp UserDAO để xử lý lưu trữ
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.addUser(user);

            if (success) {
                // Hệ thống hiển thị thông báo thành công
                JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Admin click nút OK trên thông báo -> gọi lại lớp UserManageFrm
                this.dispose();
                UserManageFrm manageFrm = new UserManageFrm();
                manageFrm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create account! Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                // Quay lại màn hình nhập liệu để Admin sửa
                this.dispose();
                AddUserFrm addFrm = new AddUserFrm();
                addFrm.setVisible(true);
            }
        } else if (e.getSource() == btnCancel) {
            this.dispose();
            AddUserFrm addFrm = new AddUserFrm();
            addFrm.setVisible(true);
        }
    }
}
