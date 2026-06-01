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
import java.time.format.DateTimeFormatter;

import java.util.List;

public class AcceptBorrowingFrm extends JFrame {
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

    public AcceptBorrowingFrm(User user, Borrowing borrowing) {
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

        
        JPanel pnlUpper = new JPanel(new BorderLayout(10, 10));
        pnlUpper.setBackground(Color.WHITE);
        pnlUpper.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu mượn"));

        JPanel pnlInfo = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlInfo.setBackground(Color.WHITE);
        lblStudentName = new JLabel("Họ tên: ");
        lblStudentId = new JLabel("Mã sinh viên: ");
        lblBorrowDate = new JLabel("Ngày hẹn nhận: ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (borrowing.getExpectedReceiveDate() != null) {
            lblBorrowDate.setText("Ngày hẹn nhận: " + borrowing.getExpectedReceiveDate().format(formatter));
        }
        if (borrowing.getStudent() != null) {
            lblStudentId.setText("Mã sinh viên: " + borrowing.getStudent().getStudentId());
            lblStudentName.setText("Họ tên: " + borrowing.getStudent().getFullName());
        }

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
        scrBooks.getViewport().setBackground(Color.WHITE);

        pnlUpper.add(pnlInfo, BorderLayout.NORTH);
        pnlUpper.add(scrBooks, BorderLayout.CENTER);

        
        JPanel pnlLower = new JPanel(new BorderLayout(10, 10));
        pnlLower.setBackground(Color.WHITE);
        pnlLower.setBorder(BorderFactory.createTitledBorder("Thông xử lý"));

        JPanel pnlStats = new JPanel(new GridLayout(1, 2));
        pnlStats.setBackground(Color.WHITE);
        lblTotalBooks = new JLabel("Tổng số sách: " + borrowing.getNumberOfBooks());
        lblTotalFine = new JLabel("Tổng tiền phạt: 0 VNĐ");
        pnlStats.add(lblTotalBooks);
        pnlStats.add(lblTotalFine);

        lblNote = new JLabel("Ghi chú: ");
        JScrollPane scrNote = new JScrollPane(lblNote);
        scrNote.getViewport().setBackground(Color.WHITE);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtns.setBackground(Color.WHITE);
        JButton btnConfirm = new JButton("Xác nhận phiếu");
        JButton btnCancel = new JButton("Hủy");
        btnConfirm.setBackground(Color.WHITE);
        btnConfirm.setForeground(Color.BLACK);
        btnConfirm.setBorder(BorderFactory.createLineBorder(new Color(160, 170, 185)));
        pnlBtns.add(btnConfirm);
        pnlBtns.add(btnCancel);

        pnlLower.add(pnlStats, BorderLayout.NORTH);
        pnlLower.add(scrNote, BorderLayout.CENTER);
        pnlLower.add(pnlBtns, BorderLayout.SOUTH);

        
        add(pnlUpper, BorderLayout.CENTER);
        add(pnlLower, BorderLayout.SOUTH);

        
        loadBookData();

        
        btnConfirm.addActionListener(e -> confirmAction());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadBookData() {
        List<BorrowedBook> borrowedBooks = borrowing.getBooks();

        
        tbmBooks.setRowCount(0);
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        
        for (int i = 0; i < borrowedBooks.size(); i++) {
            BorrowedBook bb = borrowedBooks.get(i);
            
            Book book = null;
            if (bb.getBookItem() != null) {
                book = bookDAO.findByID(bookItemDAO.getBookISBN(bb.getBookItem().getId()));
            }

            String expectedReturn = bb.getExpectedReturnDate() != null ? bb.getExpectedReturnDate().format(sdf) : "";
            
            tbmBooks.addRow(new Object[] {
                i + 1,
                book != null ? book.getISBN() : (bb.getBookItem() != null ? bb.getBookItem().getId() : ""),
                book != null ? book.getTitle() : "",
                expectedReturn,
                bb.getStatus(),
                "0"
            });
            if (book != null) {
                System.out.println("Added book: " + book.getTitle() + " to table");
            }
        }
    }

    private void confirmAction() {
        
        if (borrowing.getBooks() == null || borrowing.getBooks().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phiếu mượn không có sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!borrowing.getStatus().equals("pending")) {
            JOptionPane.showMessageDialog(this, "Phiếu mượn không ở trạng thái chờ xác nhận!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (borrowingDAO.confirmBorrowing(borrowing.getId(), java.time.LocalDate.now())) {
            
            for (BorrowedBook bb : borrowing.getBooks()) {
                if (bb.getBookItem() != null) {
                    bookItemDAO.updateStatus(bb.getBookItem().getId(), "borrowed");
                }
            }

            JOptionPane.showMessageDialog(this, "Xác nhận phiếu mượn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi xác nhận phiếu mượn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
