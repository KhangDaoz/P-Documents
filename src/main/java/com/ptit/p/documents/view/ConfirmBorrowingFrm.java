package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BorrowingDAO;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

public class ConfirmBorrowingFrm extends JFrame implements ActionListener {

    private Borrowing b;
    private JTextArea outBorrowingInfo;
    private JButton btnBack;
    private JButton btnConfirm;

    public ConfirmBorrowingFrm(Borrowing b) {
        this.b = b;
        initComponents();
    }

    private void initComponents() {
        setTitle("Đặt sách - Bước 3: Xác nhận thông tin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 360);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(6, 6));

        
        outBorrowingInfo = new JTextArea(buildInfoText());
        outBorrowingInfo.setEditable(false);
        outBorrowingInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        infoPanel.add(new JScrollPane(outBorrowingInfo), BorderLayout.CENTER);
        add(infoPanel, BorderLayout.CENTER);

        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnBack = new JButton("Quay lại");
        btnConfirm = new JButton("Lưu đặt sách");
        btnBack.addActionListener(this);
        btnConfirm.addActionListener(this);
        btnPanel.add(btnBack);
        btnPanel.add(btnConfirm);
        add(btnPanel, BorderLayout.SOUTH);

    }

    private String buildInfoText() {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();

        if (b.getStudent() != null) {
            sb.append("Sinh viên: ").append(b.getStudent().getStudentId())
              .append(" - ").append(b.getStudent().getFullName()).append("\n");
        }

        sb.append("Sách:\n");
        for (BorrowedBook bb : b.getBooks()) {
            if (bb.getBook() != null) {
                sb.append(bb.getBook().getIsbn())
                  .append(" - ").append(bb.getBook().getTitle());
                if (bb.getExpectedReturnDate() != null) {
                    sb.append(" (Ngày trả: ").append(bb.getExpectedReturnDate().format(sdf)).append(")");
                }
                sb.append("\n");
            }
        }

        if (b.getCreatedAt() != null) {
            sb.append("Ngày đặt: ").append(b.getCreatedAt().format(sdf)).append("\n");
        }
        
        if (b.getExpectedReceiveDate() != null) {
            sb.append("Ngày nhận dự kiến: ").append(b.getExpectedReceiveDate().format(sdf)).append("\n");
        }

        String statusDisplay = b.getStatus();
        if ("pending".equalsIgnoreCase(statusDisplay) || "Chờ nhận sách".equalsIgnoreCase(statusDisplay)) {
            statusDisplay = "Chờ nhận sách";
        }
        sb.append("Trạng thái phiếu: ").append(statusDisplay).append("\n");

        return sb.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) {
            btnConfirm.setEnabled(false);
            BorrowingDAO dao = new BorrowingDAO();
            boolean ok = dao.addBorrowing(b);

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Đặt sách thành công!\nMã phiếu mượn: " + b.getId(),
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                if (b.getUser() != null)
                    new LibrarianHomeFrm(b.getUser()).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Đặt sách thất bại!\nKhông còn bản sao khả dụng hoặc xảy ra lỗi hệ thống.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                btnConfirm.setEnabled(true);
            }
        } else if (e.getSource() == btnBack) {
            this.dispose();
        }
    }
}
