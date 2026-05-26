package com.library.view;

import com.library.dao.BorrowedBookDAO;
import com.library.model.BorrowedBook;

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

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final BorrowingStatFrm parent;
    private final BorrowedBookDAO dao = new BorrowedBookDAO();

    public BorrowDetailFrm(BorrowingStatFrm parent, String bookId, String bookTitle) {
        this.parent = parent;

        setTitle("BorrowDetailFrm - Chi tiết mượn trả: " + bookTitle);
        setSize(820, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel head = new JLabel("Lịch sử mượn trả - " + bookTitle + " (Mã: " + bookId + ")",
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
        List<BorrowedBook> history = dao.getBorrowHistoryByBook(bookId);
        for (BorrowedBook bb : history) {
            tm.addRow(new Object[]{
                bb.getBorrowing().getStudent().getStudentCode(),
                bb.getBorrowing().getStudent().getFullName(),
                bb.getBorrowing().getBorrowDate().format(DF),
                bb.getDueDate().format(DF),
                bb.getReturnDate() == null ? "" : bb.getReturnDate().format(DF),
                bb.getStatus()
            });
        }

        // Spec §1.b bước 35-37: Trở về -> BorrowingStatFrm vẫn đang mở
        btnBack.addActionListener(e -> dispose());
    }
}
