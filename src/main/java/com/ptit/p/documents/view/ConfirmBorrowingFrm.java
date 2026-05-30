package com.ptit.p.documents.view;

import com.ptit.p.documents.dao.BorrowingDAO;
import com.ptit.p.documents.dao.BorrowedBookDAO;
import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.dao.BookItemDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConfirmBorrowingFrm extends JFrame {
    private User currentUser;
    private Borrowing borrowing;
    private BorrowingDAO borrowingDAO;
    private BookDAO bookDAO;
    private BorrowedBookDAO borrowedBookDAO;
    private BookItemDAO bookItemDAO;

    private JLabel lblStudentName;
    private JLabel lblStudentId;
    private JLabel lblBorrowDate;
    private JTable tblBooks;
    private DefaultTableModel tbmBooks;
    private JLabel lblNote;
    private JLabel lblTotalBooks;
    private JLabel lblTotalFine;

    public ConfirmBorrowingFrm(User user, Borrowing borrowing) {
        this.currentUser = user;
        this.borrowing = borrowing;
        this.borrowingDAO = new BorrowingDAO();
        this.bookDAO = new BookDAO();
        this.borrowedBookDAO = new BorrowedBookDAO();
        this.bookItemDAO = new BookItemDAO();


        setTitle("Xác nhận phiếu mượn");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // === UPPER PANEL ===
        JPanel pnlUpper = new JPanel(new BorderLayout(10, 10));
        pnlUpper.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu mượn"));

        JPanel pnlInfo = new JPanel(new GridLayout(3, 2, 10, 10));
        lblStudentName = new JLabel("Họ tên: ");
        lblStudentId = new JLabel("Mã sinh viên: ");
        lblBorrowDate = new JLabel("Ngày mượn: ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblBorrowDate.setText("Ngày mượn: " + borrowing.getExpectedReceiveDate().format(formatter));
        lblStudentId.setText("Mã sinh viên: " + borrowing.getStudent().getId());
        lblStudentName.setText("Họ tên: " + borrowing.getStudent().getFullName());

        pnlInfo.add(lblStudentName);
        pnlInfo.add(lblStudentId);
        pnlInfo.add(lblBorrowDate);

        String[] bookCols = {"#", "Mã sách", "Tên sách", "Hạn trả", "Trạng thái", "Lỗi phạt"};
        tbmBooks = new DefaultTableModel(bookCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBooks = new JTable(tbmBooks);
        JScrollPane scrBooks = new JScrollPane(tblBooks);

        pnlUpper.add(pnlInfo, BorderLayout.NORTH);
        pnlUpper.add(scrBooks, BorderLayout.CENTER);

        // === LOWER PANEL ===
        JPanel pnlLower = new JPanel(new BorderLayout(10, 10));
        pnlLower.setBorder(BorderFactory.createTitledBorder("Thông tin xử lý"));

        JPanel pnlStats = new JPanel(new GridLayout(1, 2));
        lblTotalBooks = new JLabel("Tổng số sách: " + borrowing.getNumberOfBooks());
        lblTotalFine = new JLabel("Tổng tiền phạt: 0 VNĐ");
        pnlStats.add(lblTotalBooks);
        pnlStats.add(lblTotalFine);

        lblNote = new JLabel("Ghi chú: ");
        JScrollPane scrNote = new JScrollPane(lblNote);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnConfirm = new JButton("Xác nhận phiếu");
        JButton btnCancel = new JButton("Hủy");
        btnConfirm.setBackground(Color.GREEN);
        pnlBtns.add(btnConfirm);
        pnlBtns.add(btnCancel);

        pnlLower.add(pnlStats, BorderLayout.NORTH);
        pnlLower.add(scrNote, BorderLayout.CENTER);
        pnlLower.add(pnlBtns, BorderLayout.SOUTH);

        // === ADD TO MAIN FRAME ===
        add(pnlUpper, BorderLayout.CENTER);
        add(pnlLower, BorderLayout.SOUTH);

        // === LOAD DATA ===
        loadBookData();

        // === LISTENERS ===
        btnConfirm.addActionListener(e -> confirmAction());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadBookData() {
        List<BorrowedBook> borrowedBooks = borrowing.getBorrowedBooks();

        // Clear existing data
        tbmBooks.setRowCount(0);

        // Load books
        for (int i = 0; i < borrowedBooks.size(); i++) {
            BorrowedBook bb = borrowedBooks.get(i);
            Book book = bookDAO.findByID(new BookItemDAO().getBookISBN(bb.getBookItem().getId()));

            // Calculate fine for this book

            tbmBooks.addRow(new Object[] {
                i + 1,
                book.getId(),
                book.getTitle(),
                bb.getExpectedReturnDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                bb.getStatus()
            });
            System.out.println("Added book: " + book.getTitle() + "to table");
        }
    }

    private void confirmAction() {
        // Validate
        if (borrowing.getBorrowedBooks().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phiếu mượn không có sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(!borrowing.getStatus().equals("pending")) {
            JOptionPane.showMessageDialog(this, "Phiếu mượn không ở trạng thái chờ xác nhận!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create borrowing record
        // borrowing.setStatus("BORROWING");
        // borrowing.setUser(currentUser);

        if (borrowingDAO.updateBorrowingStatus(borrowing.getId(), "borrowed")) {
            // Update book statuses to BORROWING
            for (BorrowedBook bb : borrowing.getBorrowedBooks()) {
                bookItemDAO.updateStatus(bb.getBookItem().getId(), "borrowed");
            }

            JOptionPane.showMessageDialog(this, "Xác nhận phiếu mượn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi xác nhận phiếu mượn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
