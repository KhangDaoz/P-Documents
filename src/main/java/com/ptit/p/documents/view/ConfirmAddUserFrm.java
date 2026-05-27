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
        setSize(660, 460);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color color1 = new Color(226, 232, 240);
                Color color2 = new Color(148, 163, 184);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 390));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
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
        pnlGrid.add(createGridLabel(user.getPassword(), Color.WHITE));

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
        } else if ("librarian".equalsIgnoreCase(roleText)) {
            roleText = "Librarian";
        }
        pnlGrid.add(createGridLabel(roleText, Color.WHITE));

        pnl.add(pnlGrid);

        // Nút Xác nhận & Quay lại
        btnCancel = new JButton("Back");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setBackground(Color.WHITE);
        btnCancel.setForeground(new Color(50, 60, 70));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.setBounds(130, 280, 140, 35);
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

        btnConfirm = new JButton("Confirm & Save");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setBackground(new Color(96, 165, 250));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorder(BorderFactory.createEmptyBorder());
        btnConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirm.setBounds(330, 280, 140, 35);
        btnConfirm.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnConfirm.setBackground(new Color(59, 130, 246));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnConfirm.setBackground(new Color(96, 165, 250));
            }
        });
        btnConfirm.addActionListener(this);
        pnl.add(btnConfirm);

        // JTable is not used anymore in visual, keep dummy to satisfy other references
        tblAddUserConfirm = new JTable();
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
            AddUserFrm addFrm = new AddUserFrm(user);
            addFrm.setVisible(true);
        }
    }
}
