package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BorrowingStatDAO;
import com.ptit.p.documents.model.BorrowingStat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Giao diện tùy chọn thống kê sách mượn nhiều (spec §1.a, §1.b bước 9-20, 38-42).
 * - Nhập khoảng thời gian (yyyy-MM-dd) và top N
 * - Bấm "Thống kê" -> gọi BorrowingStatDAO.getTopBorrowedBooks()
 * - Click dòng kết quả -> mở BorrowDetailFrm
 * - "Xuất Excel" -> gọi BorrowingStatDAO.exportToExcel()
 */
public class BorrowingStatFrm extends JFrame {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final StatMenuFrm parent;
    private final BorrowingStatDAO dao = new BorrowingStatDAO();

    private final JTextField txtFrom = new JTextField("2026-01-01", 10);
    private final JTextField txtTo   = new JTextField(LocalDate.now().toString(), 10);
    private final JSpinner spnTopN   = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new String[]{"Mã sách", "Tên sách", "Tác giả", "Thể loại", "Lượt mượn"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private List<BorrowingStat> currentRows = new java.util.ArrayList<>();

    public BorrowingStatFrm(StatMenuFrm parent) {
        this.parent = parent;

        setTitle("BorrowingStatFrm - Thống kê sách mượn nhiều");
        setSize(880, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ----- TOP: filter panel -----
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filter.setBorder(BorderFactory.createTitledBorder("Tùy chọn thống kê"));
        filter.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        filter.add(txtFrom);
        filter.add(new JLabel("Đến ngày:"));
        filter.add(txtTo);
        filter.add(new JLabel("Top N:"));
        filter.add(spnTopN);

        JButton btnSearch = new JButton("Thống kê");
        JButton btnExport = new JButton("Xuất Excel");
        JButton btnBack   = new JButton("Trở về");
        filter.add(btnSearch);
        filter.add(btnExport);
        filter.add(btnBack);

        add(filter, BorderLayout.NORTH);

        // ----- CENTER: result table -----
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ----- Action handlers (spec §1.b bước 12, 22, 35-39) -----
        btnSearch.addActionListener(e -> doSearch());
        btnExport.addActionListener(e -> doExport());
        btnBack.addActionListener(e -> {
            dispose();
            if (parent != null) parent.setVisible(true);
        });

        // Click dòng để xem chi tiết (spec §1.b bước 21-23)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            if (row >= 0 && row < currentRows.size()) {
                BorrowingStat s = currentRows.get(row);
                // Mở chi tiết khi user click
                SwingUtilities.invokeLater(() ->
                    new BorrowDetailFrm(this, s.getBookId(), s.getTitle()).setVisible(true));
                table.clearSelection();
            }
        });
    }

    private void doSearch() {
        try {
            LocalDate from = LocalDate.parse(txtFrom.getText().trim(), DF);
            LocalDate to   = LocalDate.parse(txtTo.getText().trim(), DF);
            int topN = (Integer) spnTopN.getValue();

            currentRows = dao.getTopBorrowedBooks(from, to, topN);
            tableModel.setRowCount(0);
            for (BorrowingStat s : currentRows) {
                tableModel.addRow(new Object[]{
                    s.getBookId(), s.getTitle(), s.getAuthor(), s.getCategory(), s.getBorrowCount()
                });
            }
            if (currentRows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu trong khoảng thời gian đã chọn.");
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Định dạng ngày không hợp lệ (yyyy-MM-dd)", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doExport() {
        if (currentRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để xuất. Vui lòng bấm Thống kê trước.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("thong-ke-sach-muon-nhieu.xlsx"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xlsx")) path += ".xlsx";
            boolean ok = dao.exportToExcel(currentRows, path);
            if (ok)
                JOptionPane.showMessageDialog(this, "Xuất file Excel thành công:\n" + path);
            else
                JOptionPane.showMessageDialog(this, "Xuất file Excel thất bại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
