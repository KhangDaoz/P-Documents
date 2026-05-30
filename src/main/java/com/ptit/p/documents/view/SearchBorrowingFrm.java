package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BorrowingDAO;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Giao dien tim kiem phieu muon — Buoc dau cua module Huy Dat Sach.
 */
public class SearchBorrowingFrm extends JFrame implements ActionListener {

    private User       u;
    private JTextField txtSearch;
    private JButton    btnSearch;
    private JTable     tblListBorrow;
    private JLabel     lblStatus;

    private DefaultTableModel    tableModel;
    private ArrayList<Borrowing> searchResults = new ArrayList<>();

    public SearchBorrowingFrm(User u) {
        this.u = u;
        initComponents();
    }

    private void initComponents() {
        setTitle("Huy dat sach — Tim kiem phieu muon");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(780, 440);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(4, 4));

        // ---- Panel tìm kiếm ----
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tim kiem phieu muon (trang thai: pending)"));

        searchPanel.add(new JLabel("Ma SV / Ho ten:"));
        txtSearch = new JTextField(22);
        searchPanel.add(txtSearch);

        btnSearch = new JButton("Tim kiem");
        btnSearch.addActionListener(this);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);

        // ---- Bảng kết quả ----
        String[] cols = {"ID Phieu", "Ma SV", "Ho ten SV", "Ten sach", "Ngay dat", "Trang thai"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblListBorrow = new JTable(tableModel);
        tblListBorrow.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblListBorrow.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) onBorrowingSelected();
            }
        });

        add(new JScrollPane(tblListBorrow), BorderLayout.CENTER);

        lblStatus = new JLabel("Nhap Ma SV hoac ho ten va nhan Tim kiem. Double-click de huy phieu.");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        add(lblStatus, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            BorrowingDAO dao = new BorrowingDAO();
            searchResults = dao.searchBorrowing(txtSearch.getText().trim());

            tableModel.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Borrowing b : searchResults) {
                String tenSach = "";
                if (b.getBooks() != null && !b.getBooks().isEmpty()
                        && b.getBooks().get(0).getBook() != null)
                    tenSach = b.getBooks().get(0).getBook().getTitle();

                tableModel.addRow(new Object[]{
                        b.getId(),
                        b.getStudent() != null ? b.getStudent().getStudentId() : "",
                        b.getStudent() != null ? b.getStudent().getFullName()  : "",
                        tenSach,
                        b.getCreatedAt() != null ? sdf.format(b.getCreatedAt()) : "",
                        b.getStatus()
                });
            }

            lblStatus.setText(searchResults.isEmpty()
                    ? "Khong tim thay phieu nao dang cho nhan sach."
                    : "Tim thay " + searchResults.size() + " phieu. Double-click de chon phieu can huy.");
        }
    }

    private void onBorrowingSelected() {
        int row = tblListBorrow.getSelectedRow();
        if (row < 0 || row >= searchResults.size()) return;
        new ConfirmCancelFrm(searchResults.get(row), u).setVisible(true);
        this.dispose();
    }
}
