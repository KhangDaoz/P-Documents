package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.StockStatDAO;
import com.ptit.p.documents.model.StockStat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Giao diện báo cáo sách hư hỏng / thất lạc (spec §2.a, §2.b bước 9-27).
 *
 * NOTE: spec §2.a "Tầng giao diện" có ghi tên "StockReportFrm" trong bảng View,
 * nhưng spec §2.b bước 8-9 lại gọi là "StockStatFrm". Code dùng "StockStatFrm"
 * theo đúng tên trong biểu đồ tuần tự (đã thống nhất với người dùng).
 *
 * - Nhập khoảng thời gian + lý do (Tất cả/Hư hỏng/Thất lạc)
 * - "Tìm kiếm" -> gọi StockStatDAO.searchDamageLossRecords()
 * - "In báo cáo PDF" -> gọi StockStatDAO.exportToPDF()
 */
public class StockStatFrm extends JFrame {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final StatMenuFrm parent;
    private final StockStatDAO dao = new StockStatDAO();

    private final JTextField txtFrom = new JTextField("2026-01-01", 10);
    private final JTextField txtTo   = new JTextField(LocalDate.now().toString(), 10);
    private final JComboBox<String> cbReason = new JComboBox<>(
        new String[]{"Tất cả", "Hư hỏng", "Thất lạc"});

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new String[]{"Tên sách", "Mã vạch", "Tình trạng"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    private List<StockStat> currentRows = new java.util.ArrayList<>();

    public StockStatFrm(StatMenuFrm parent) {
        this.parent = parent;

        setTitle("StockStatFrm - Báo cáo sách hư hỏng / thất lạc");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filter.setBorder(BorderFactory.createTitledBorder("Lọc tra cứu"));
        filter.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        filter.add(txtFrom);
        filter.add(new JLabel("Đến ngày:"));
        filter.add(txtTo);
        filter.add(new JLabel("Lý do:"));
        filter.add(cbReason);

        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnPdf    = new JButton("In báo cáo PDF");
        JButton btnBack   = new JButton("Trở về");
        filter.add(btnSearch);
        filter.add(btnPdf);
        filter.add(btnBack);

        add(filter, BorderLayout.NORTH);

        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> doSearch());
        btnPdf.addActionListener(e -> doExport());
        btnBack.addActionListener(e -> {
            dispose();
            if (parent != null) parent.setVisible(true);
        });
    }

    private void doSearch() {
        try {
            LocalDate from = LocalDate.parse(txtFrom.getText().trim(), DF);
            LocalDate to   = LocalDate.parse(txtTo.getText().trim(), DF);
            String reason  = (String) cbReason.getSelectedItem();

            currentRows = dao.searchDamageLossRecords(from, to, reason);
            tableModel.setRowCount(0);
            for (StockStat s : currentRows) {
                // Spec §2.b bước 15-18: BookItem truy cập qua book.getItems()
                String barcode = s.getBook().getItems().isEmpty()
                        ? "" : s.getBook().getItems().get(0).getId();
                tableModel.addRow(new Object[]{
                    s.getBook().getTitle(),
                    barcode,
                    s.getReason()
                });
            }
            if (currentRows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có bản ghi phù hợp.");
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Định dạng ngày không hợp lệ (yyyy-MM-dd)", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doExport() {
        if (currentRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có dữ liệu để xuất. Bấm Tìm kiếm trước.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("bao-cao-hu-hong-that-lac.pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) path += ".pdf";
            boolean ok = dao.exportToPDF(currentRows, path);
            if (ok)
                JOptionPane.showMessageDialog(this, "Tải file PDF thành công:\n" + path);
            else
                JOptionPane.showMessageDialog(this, "Xuất file PDF thất bại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
