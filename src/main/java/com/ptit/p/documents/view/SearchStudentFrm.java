package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.StudentDAO;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Giao dien tim kiem sinh vien — Buoc 2 cua module Dat Sach.
 */
public class SearchStudentFrm extends JFrame implements ActionListener {

    private Borrowing b;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnAddStudent;
    private JTable tblListStudent;
    private JLabel lblStatus;

    private DefaultTableModel tableModel;
    private ArrayList<Student> searchResults = new ArrayList<>();

    public SearchStudentFrm(Borrowing b) {
        this.b = b;
        initComponents();
    }

    private void initComponents() {
        setTitle("Dat sach — Buoc 2: Tim kiem sinh vien");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(680, 440);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(4, 4));

        // ---- Panel tìm kiếm ----
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tim kiem sinh vien"));

        searchPanel.add(new JLabel("Ma SV / Ho ten:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);

        btnSearch = new JButton("Tim kiem");
        btnSearch.addActionListener(this);
        searchPanel.add(btnSearch);

        btnAddStudent = new JButton("Them sinh vien moi");
        btnAddStudent.addActionListener(this);
        searchPanel.add(btnAddStudent);

        add(searchPanel, BorderLayout.NORTH);

        // ---- Bảng kết quả ----
        String[] cols = { "Ma sinh vien", "Ho ten", "Email", "So dien thoai", "Dia chi" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblListStudent = new JTable(tableModel);
        tblListStudent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblListStudent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    onStudentSelected();
            }
        });

        add(new JScrollPane(tblListStudent), BorderLayout.CENTER);

        // ---- Status ----
        lblStatus = new JLabel("Nhap tu khoa va nhan Tim kiem. Double-click de chon sinh vien.");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        add(lblStatus, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            StudentDAO dao = new StudentDAO();
            searchResults = dao.searchStudent(txtSearch.getText().trim());
            tableModel.setRowCount(0);
            for (Student st : searchResults) {
                tableModel.addRow(new Object[] {
                        st.getStudentId(), st.getFullName(),
                        st.getEmail(), st.getPhone(), st.getAddress()
                });
            }
            lblStatus.setText(searchResults.isEmpty()
                    ? "Khong tim thay sinh vien phu hop."
                    : "Tim thay " + searchResults.size() + " sinh vien. Double-click de chon.");
        } else if (e.getSource() == btnAddStudent) {
            new AddStudentFrm(b).setVisible(true);
        }
    }

    private void onStudentSelected() {
        int row = tblListStudent.getSelectedRow();
        if (row < 0 || row >= searchResults.size())
            return;
        b.setStudent(searchResults.get(row));
        new ConfirmBorrowingFrm(b).setVisible(true);
        this.dispose();
    }
}
