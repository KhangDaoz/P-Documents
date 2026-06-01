package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BorrowingDAO;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;

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

    
    public ConfirmCancelFrm(Borrowing b) {
        this(b, null);
    }

    private void initComponents() {
        setTitle("Hủy đặt sách - Xác nhận hủy phiếu");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(6, 6));

        
        outInfo = new JTextArea(buildInfoText());
        outInfo.setEditable(false);
        outInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        infoPanel.add(new JScrollPane(outInfo), BorderLayout.CENTER);

        JLabel lblWarn = new JLabel("Sau khi hủy, thao tác này không thể hoàn tác.");
        lblWarn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        infoPanel.add(lblWarn, BorderLayout.SOUTH);

        add(infoPanel, BorderLayout.CENTER);

        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnBack    = new JButton("Quay lại");
        btnConfirm = new JButton("Xác nhận hủy");
        btnBack.addActionListener(this);
        btnConfirm.addActionListener(this);
        btnPanel.add(btnBack);
        btnPanel.add(btnConfirm);
        add(btnPanel, BorderLayout.SOUTH);

    }

    private String buildInfoText() {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("THÔNG TIN HỦY ĐẶT SÁCH\n");
        sb.append("-------------------------------------\n\n");

        if (b.getStudent() != null) {
                        sb.append("Sinh viên       :  ").append(b.getStudent().getStudentId())
                            .append("  -  ").append(b.getStudent().getFullName()).append("\n");
        }
        sb.append("Mã phiếu mượn   :  ").append(b.getId()).append("\n");

        for (BorrowedBook bb : b.getBooks()) {
            if (bb.getBook() != null) {
                                sb.append("Sách            :  ").append(bb.getBook().getIsbn())
                                    .append("  -  ").append(bb.getBook().getTitle()).append("\n");
            }
        }

        if (b.getCreatedAt() != null)
            sb.append("Ngày đặt mượn   :  ").append(b.getCreatedAt().format(sdf)).append("\n");

        sb.append("Trạng thái hiện :  ").append(b.getStatus()).append("\n");
        return sb.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn hủy phiếu mượn " + b.getId() + " không?\n"
                    + "Thao tác này không thể hoàn tác.",
                    "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                btnConfirm.setEnabled(false);
                BorrowingDAO dao = new BorrowingDAO();
                boolean ok = dao.cancelBorrowing(b.getId());

                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Hủy đặt sách thành công!\nPhiếu " + b.getId() + " đã chuyển sang trạng thái 'cancelled'.",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    if (u != null)
                        new LibrarianHomeFrm(u).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Hủy thất bại!\nPhiếu mượn có thể đã thay đổi trạng thái hoặc xảy ra lỗi hệ thống.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    btnConfirm.setEnabled(true);
                }
            }
        } else if (e.getSource() == btnBack) {
            this.dispose();
        }
    }
}
