package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BorrowingDAO;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

/**
 * Giao dien xac nhan thong tin dat sach — Buoc cuoi cua module Dat Sach.
 */
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
        setTitle("Dat sach — Buoc 3: Xac nhan thong tin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 360);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(6, 6));

        // ---- Thông tin xác nhận ----
        outBorrowingInfo = new JTextArea(buildInfoText());
        outBorrowingInfo.setEditable(false);
        outBorrowingInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        infoPanel.add(new JScrollPane(outBorrowingInfo), BorderLayout.CENTER);
        add(infoPanel, BorderLayout.CENTER);

        // ---- Nút ----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnBack = new JButton("Quay lai");
        btnConfirm = new JButton("Luu dat sach");
        btnBack.addActionListener(this);
        btnConfirm.addActionListener(this);
        btnPanel.add(btnBack);
        btnPanel.add(btnConfirm);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private String buildInfoText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("THONG TIN DAT SACH\n");
        sb.append("-------------------------------------\n\n");

        if (b.getStudent() != null) {
            sb.append("Sinh vien    :  ").append(b.getStudent().getStudentId())
                    .append("  -  ").append(b.getStudent().getFullName()).append("\n");
            if (b.getStudent().getPhone() != null && !b.getStudent().getPhone().isEmpty())
                sb.append("Dien thoai   :  ").append(b.getStudent().getPhone()).append("\n");
        }
        sb.append("\n");

        for (BorrowedBook bb : b.getBooks()) {
            if (bb.getBook() != null) {
                sb.append("Sach         :  ").append(bb.getBook().getIsbn())
                        .append("  -  ").append(bb.getBook().getTitle()).append("\n");
                sb.append("Tac gia      :  ").append(bb.getBook().getAuthor()).append("\n");
            }
        }
        sb.append("\n");

        if (b.getCreatedAt() != null)
            sb.append("Ngay dat     :  ").append(sdf.format(b.getCreatedAt())).append("\n");
        if (b.getExpectedReceiveDate() != null)
            sb.append("Nhan du kien :  ").append(sdf.format(b.getExpectedReceiveDate())).append("\n");

        sb.append("Trang thai   :  ").append(b.getStatus()).append("\n");
        if (b.getUser() != null)
            sb.append("Thu thu      :  ").append(b.getUser().getFullName()).append("\n");

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
                        "Dat sach thanh cong!\nMa phieu muon: " + b.getId(),
                        "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                if (b.getUser() != null)
                    new LibrarianHomeFrm(b.getUser()).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Dat sach that bai!\nKhong con ban sao kha dung hoac xay ra loi he thong.",
                        "Loi", JOptionPane.ERROR_MESSAGE);
                btnConfirm.setEnabled(true);
            }
        } else if (e.getSource() == btnBack) {
            this.dispose();
        }
    }
}
