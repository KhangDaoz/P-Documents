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
        super("Xác nhận tài khoản mới");
        this.user = user;

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

       
        JLabel lblHeader = new JLabel("Xác nhận thông tin tài khoản mới", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(new Color(50, 60, 70));
        lblHeader.setBounds(100, 30, 400, 35);
        pnl.add(lblHeader);

        
        JPanel pnlGrid = new JPanel(new GridLayout(5, 2, 0, 0));
        pnlGrid.setBounds(100, 75, 400, 175);
        pnlGrid.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        
        pnlGrid.add(createGridLabel("Họ và tên", Color.WHITE));
        pnlGrid.add(createGridLabel(user.getFullName(), Color.WHITE));

        
        pnlGrid.add(createGridLabel("Tên đăng nhập", Color.WHITE));
        pnlGrid.add(createGridLabel(user.getUsername(), Color.WHITE));

        
        pnlGrid.add(createGridLabel("Mật khẩu", Color.WHITE));
        pnlGrid.add(createGridLabel(user.getPassword(), Color.WHITE));

        
        pnlGrid.add(createGridLabel("Số điện thoại", Color.WHITE));
        pnlGrid.add(createGridLabel(user.getPhone(), Color.WHITE));

        
        pnlGrid.add(createGridLabel("Quyền hạn", Color.WHITE));
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

        
        btnCancel = new JButton("Quay lại");
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

        btnConfirm = new JButton("Xác nhận và lưu");
        btnConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnConfirm.setBackground(Color.WHITE);
        btnConfirm.setForeground(Color.BLACK);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        btnConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirm.setBounds(330, 280, 140, 35);
        btnConfirm.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnConfirm.setBackground(new Color(230, 235, 240));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnConfirm.setBackground(Color.WHITE);
            }
        });
        btnConfirm.addActionListener(this);
        pnl.add(btnConfirm);

        
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
            
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.addUser(user);

            if (success) {
                
                JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                this.dispose();
                UserManageFrm manageFrm = new UserManageFrm();
                manageFrm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Tạo tài khoản thất bại! Tên đăng nhập có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                
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
