package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BorrowingDAO;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

/**
 * Giao dien xac nhan huy dat sach — Buoc cuoi cua module Huy Dat Sach.
 */
public class ConfirmCancelFrm extends JFrame implements ActionListener {

    private Borrowing b;
    private User      u;
    private JTextArea outInfo;
    private JButton   btnBack;
    private JButton   btnConfirm;

    public ConfirmCancelFrm(Borrowing b, User u) {
        this.b = b;
        this.u = u;
        initComponents();
    }

    /** Constructor tuong thich cu khong co User. */
    public ConfirmCancelFrm(Borrowing b) {
        this(b, null);
    }

    private void initComponents() {
        setTitle("Huy dat sach — Xac nhan huy phieu");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(6, 6));

        // ---- Thông tin phiếu ----
        outInfo = new JTextArea(buildInfoText());
        outInfo.setEditable(false);
        outInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        infoPanel.add(new JScrollPane(outInfo), BorderLayout.CENTER);

        JLabel lblWarn = new JLabel("Sau khi huy, thao tac nay khong the hoan tac.");
        lblWarn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        infoPanel.add(lblWarn, BorderLayout.SOUTH);

        add(infoPanel, BorderLayout.CENTER);

        // ---- Nút ----
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnBack    = new JButton("Quay lai");
        btnConfirm = new JButton("Xac nhan huy");
        btnBack.addActionListener(this);
        btnConfirm.addActionListener(this);
        btnPanel.add(btnBack);
        btnPanel.add(btnConfirm);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private String buildInfoText() {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("THONG TIN HUY DAT SACH\n");
        sb.append("-------------------------------------\n\n");

        if (b.getStudent() != null) {
            sb.append("Sinh vien       :  ").append(b.getStudent().getStudentId())
              .append("  -  ").append(b.getStudent().getFullName()).append("\n");
        }
        sb.append("Ma phieu muon   :  ").append(b.getId()).append("\n");

        for (BorrowedBook bb : b.getBooks()) {
            if (bb.getBook() != null) {
                sb.append("Sach            :  ").append(bb.getBook().getIsbn())
                  .append("  -  ").append(bb.getBook().getTitle()).append("\n");
            }
        }

        if (b.getCreatedAt() != null)
            sb.append("Ngay dat muon   :  ").append(b.getCreatedAt().format(sdf)).append("\n");

        sb.append("Trang thai hien :  ").append(b.getStatus()).append("\n");
        return sb.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ban co chac chan muon huy phieu muon " + b.getId() + " khong?\n"
                    + "Thao tac nay khong the hoan tac.",
                    "Xac nhan huy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                btnConfirm.setEnabled(false);
                BorrowingDAO dao = new BorrowingDAO();
                boolean ok = dao.cancelBorrowing(b.getId());

                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Huy dat sach thanh cong!\nPhieu " + b.getId() + " da chuyen sang trang thai 'cancelled'.",
                            "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                    if (u != null)
                        new LibrarianHomeFrm(u).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Huy that bai!\nPhieu muon co the da thay doi trang thai hoac xay ra loi he thong.",
                            "Loi", JOptionPane.ERROR_MESSAGE);
                    btnConfirm.setEnabled(true);
                }
            }
        } else if (e.getSource() == btnBack) {
            this.dispose();
        }
    }
}
