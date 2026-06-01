package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BorrowedBookDAO;
import com.ptit.p.documents.model.BorrowedBook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Chi tiết lịch sử mượn trả của một cuốn sách (spec §1.b bước 23-37).
 * 6 cột theo spec §1.b bước 34: mã SV, tên SV, ngày mượn, hạn trả, ngày trả, trạng thái.
 */
public class BorrowDetailFrm extends JFrame {

    private static final DateTimeFormatter SDF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final BorrowingStatFrm parent;
    private final BorrowedBookDAO dao = new BorrowedBookDAO();

    public BorrowDetailFrm(BorrowingStatFrm parent, String isbn, String bookTitle) {
        this.parent = parent;

        setTitle("Chi tiết mượn trả - " + bookTitle);
        setSize(820, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel head = new JLabel("Lịch sử mượn trả - " + bookTitle + " (ISBN: " + isbn + ")",
                                 SwingConstants.CENTER);
        head.setFont(head.getFont().deriveFont(Font.BOLD, 14f));
        head.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(head, BorderLayout.NORTH);

        DefaultTableModel tm = new DefaultTableModel(
            new String[]{"Mã SV", "Tên SV", "Ngày mượn", "Hạn trả", "Ngày trả", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tm);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Trở về");
        south.add(btnBack);
        add(south, BorderLayout.SOUTH);

        // Spec §1.b bước 25-33: gọi DAO ngay khi khởi tạo
        List<BorrowedBook> history = dao.getBorrowHistoryByBook(isbn);
        for (BorrowedBook bb : history) {
            tm.addRow(new Object[]{
                bb.getBorrowing().getStudent().getId(),
                bb.getBorrowing().getStudent().getFullName(),
                bb.getBorrowing().getCreatedAt() != null ? bb.getBorrowing().getCreatedAt().format(SDF) : "",
                bb.getExpectedReturnDate() != null ? bb.getExpectedReturnDate().format(SDF) : "",
                bb.getActualReturnDate() == null ? "" : bb.getActualReturnDate().format(SDF),
                bb.getStatus()
            });
        }

        // Spec §1.b bước 35-37: Trở về -> BorrowingStatFrm vẫn đang mở
        btnBack.addActionListener(e -> dispose());

    }
}
