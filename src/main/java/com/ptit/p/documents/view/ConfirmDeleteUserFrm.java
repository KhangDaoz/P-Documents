package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.UserDAO;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfirmDeleteUserFrm extends JFrame implements ActionListener {
    private final User user;
    private final JTable tblDeleteUserConfirm;
    private final JButton btnConfirm;
    private final JButton btnCancel;

    public ConfirmDeleteUserFrm(User user) {
        super("ConfirmDeleteUserFrm");
        this.user = user;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 490);
        setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setBackground(new Color(240, 242, 245));
        setContentPane(pnlMain);

        JPanel pnl = new JPanel(null);
        pnl.setPreferredSize(new Dimension(600, 420));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        pnlMain.add(pnl);

        // Tiêu đề form (hiển thị phẳng đẹp, không giống nút bấm)
        JLabel lblHeader = new JLabel("Confirm Delete User Account", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(30, 41, 59));
        lblHeader.setBounds(100, 30, 400, 35);
        pnl.add(lblHeader);

        // Grid panel cho bảng thông tin
        JPanel pnlGrid = new JPanel(new GridLayout(6, 2, 0, 0));
        pnlGrid.setBounds(100, 75, 400, 210);
        pnlGrid.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        // Dòng 1: MNV
        pnlGrid.add(createGridLabel("Employee ID", Color.WHITE));
        String mnvStr = "NV" + String.format("%03d", user.getId());
        pnlGrid.add(createGridLabel(mnvStr, new Color(248, 250, 252)));

        // Dòng 2: Họ tên
        pnlGrid.add(createGridLabel("Full Name", Color.WHITE));
        pnlGrid.add(createGridLabel(user.getFullName(), Color.WHITE));

        // Dòng 3: Tên đăng nhập
        pnlGrid.add(createGridLabel("Username", Color.WHITE));
        pnlGrid.add(createGridLabel(user.getUsername(), Color.WHITE));

        // Dòng 4: Mật khẩu
        pnlGrid.add(createGridLabel("Password", Color.WHITE));
        pnlGrid.add(createGridLabel("********", Color.WHITE));

        // Dòng 5: Số điện thoại
        pnlGrid.add(createGridLabel("Phone", Color.WHITE));
        pnlGrid.add(createGridLabel(user.getPhone(), Color.WHITE));

        // Dòng 6: Quyền hạn
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

        // Nút Huỷ và Xác nhận xoá
        btnCancel = new JButton("Cancel");
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

        btnConfirm = new JButton("Confirm");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setBackground(new Color(96, 165, 250));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorder(BorderFactory.createEmptyBorder());
        btnConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirm.setBounds(330, 310, 140, 35);
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
        tblDeleteUserConfirm = new JTable();
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
            // Gọi UserDAO để thực hiện lệnh xoá bản ghi tương ứng
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.deleteUser(user);

            if (success) {
                // Hệ thống hiển thị thông báo "Xoá tài khoản thành công"
                JOptionPane.showMessageDialog(this, "Account deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                UserManageFrm manageFrm = new UserManageFrm();
                manageFrm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete account or account not found!", "Error", JOptionPane.ERROR_MESSAGE);
                // Admin click nút OK -> gọi lại lớp SearchUserFrm để cập nhật trạng thái
                this.dispose();
                SearchUserFrm searchFrm = new SearchUserFrm(SearchUserFrm.Mode.DELETE);
                searchFrm.setVisible(true);
            }
        } else if (e.getSource() == btnCancel) {
            this.dispose();
            SearchUserFrm searchFrm = new SearchUserFrm(SearchUserFrm.Mode.DELETE);
            searchFrm.setVisible(true);
        }
    }
}
