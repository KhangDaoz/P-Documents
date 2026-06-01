package com.ptit.p.documents.view;

import java.time.format.DateTimeFormatter;

import com.ptit.p.documents.dao.BookDAO;
import com.ptit.p.documents.model.Book;
import com.ptit.p.documents.model.BorrowedBook;
import com.ptit.p.documents.model.Borrowing;
import com.ptit.p.documents.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Giao diện chon sach cho phieu muon — Buoc 1 cua module Dat Sach.
 */
public class SearchBorrowFrm extends JFrame implements ActionListener {
    // UI field for expected return date
    private JTextField txtReturnDate;
    // Stored expected return date
    private LocalDate expectedReturnDate;

    private User      u;
    private JTextField txtBookName;
    private JTextField txtAuthor;
    private JTextField txtGenre;
    private JTextField txtISBN;
    private JButton    btnSearch;
    private JTable     tblListBook;
    private JButton    btnAddToCart;

    private JTable     tblCart;
    private JButton    btnNext;

    private DefaultTableModel tableModel;
    private DefaultTableModel cartModel;
    private ArrayList<Book>   searchResults = new ArrayList<>();
    private Borrowing         currentBorrowing;

    public SearchBorrowFrm(User u) {
        this.u = u;
        LocalDate today = LocalDate.now();
        LocalDate receiveDate = today.plusDays(2);
        // Default return date is 14 days from now
        this.expectedReturnDate = today.plusDays(14);
        currentBorrowing = new Borrowing(null, u, today, receiveDate);
        initComponents();
        // Populate return date field after UI components are created
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (expectedReturnDate != null) {
            txtReturnDate.setText(expectedReturnDate.format(dtf));
        }
    }

    private void initComponents() {
        setTitle("Đặt sách - Bước 1: Chọn sách");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(4, 4));

        // ---- Panel tìm kiếm ----
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm sách"));

        searchPanel.add(new JLabel("Tên sách:"));
        txtBookName = new JTextField(12);
        searchPanel.add(txtBookName);

        searchPanel.add(new JLabel("Tác giả:"));
        txtAuthor = new JTextField(10);
        searchPanel.add(txtAuthor);

        searchPanel.add(new JLabel("Thể loại:"));
        txtGenre = new JTextField(8);
        searchPanel.add(txtGenre);

        searchPanel.add(new JLabel("ISBN:"));
        txtISBN = new JTextField(8);
        searchPanel.add(txtISBN);

        btnSearch = new JButton("Tìm kiếm");
        btnSearch.addActionListener(this);
        searchPanel.add(btnSearch);

        // ---- Panel thông tin ngày trả dự kiến ----
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        datePanel.setBorder(BorderFactory.createTitledBorder("Thông tin ngày"));
        datePanel.add(new JLabel("Ngày trả dự kiến:"));
        txtReturnDate = new JTextField(12);
        datePanel.add(txtReturnDate);

        // Combine datePanel and searchPanel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(datePanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // ---- Bảng kết quả tìm kiếm ----
        String[] searchCols = {"ISBN", "Ten sach", "Tac gia", "The loai", "Con lai"};
        tableModel = new DefaultTableModel(searchCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblListBook = new JTable(tableModel);
        tblListBook.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnAddToCart = new JButton("Thêm vào giỏ");
        btnAddToCart.addActionListener(this);

        JPanel resultPanel = new JPanel(new BorderLayout(2, 2));
        resultPanel.setBorder(BorderFactory.createTitledBorder("Kết quả tìm kiếm"));
        resultPanel.add(new JScrollPane(tblListBook), BorderLayout.CENTER);
        
        JPanel addToCartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addToCartPanel.add(btnAddToCart);
        resultPanel.add(addToCartPanel, BorderLayout.SOUTH);

        // ---- Bảng giỏ sách ----
        String[] cartCols = {"STT", "ISBN", "Tên sách", "Tác giả", "Giá (VND)"};
        cartModel = new DefaultTableModel(cartCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCart = new JTable(cartModel);
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel cartPanel = new JPanel(new BorderLayout(2, 2));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Giỏ sách đã chọn"));
        cartPanel.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        // ---- SplitPane ----
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultPanel, cartPanel);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);

        // ---- Footer ----
        btnNext = new JButton("Tiếp theo: Chọn sinh viên");
        btnNext.setEnabled(false);
        btnNext.addActionListener(this);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.add(btnNext);
        add(footerPanel, BorderLayout.SOUTH);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            BookDAO dao = new BookDAO();
            searchResults = dao.searchBook(
                    txtBookName.getText().trim(),
                    txtAuthor.getText().trim(),
                    txtGenre.getText().trim(),
                    txtISBN.getText().trim());

            tableModel.setRowCount(0);
            for (Book b : searchResults) {
                tableModel.addRow(new Object[]{
                        b.getIsbn(), b.getTitle(), b.getAuthor(),
                        b.getGenre(), b.getAvailableCopies()
                });
            }
        } else if (e.getSource() == btnAddToCart) {
            addBookToCart();
        } else if (e.getSource() == btnNext) {
            if (currentBorrowing.getBooks().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng thêm ít nhất 1 cuốn sách vào phiếu mượn.",
                        "Giỏ sách trống", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new SearchStudentFrm(currentBorrowing).setVisible(true);
        }
    }

    private void addBookToCart() {
        int row = tblListBook.getSelectedRow();
        if (row < 0 || row >= searchResults.size()) return;

        Book selected = searchResults.get(row);

        if (selected.getAvailableCopies() == 0) {
            JOptionPane.showMessageDialog(this,
                    "\"" + selected.getTitle() + "\" hiện không còn bản sao khả dụng.",
                    "Hết sách", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (BorrowedBook bb : currentBorrowing.getBooks()) {
            if (bb.getBook() != null && bb.getBook().getIsbn().equals(selected.getIsbn())) {
                JOptionPane.showMessageDialog(this,
                        "\"" + selected.getTitle() + "\" đã có trong phiếu mượn rồi!",
                        "Trùng sách", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        LocalDate returnDate;
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            returnDate = LocalDate.parse(txtReturnDate.getText().trim(), dtf);
            if (returnDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this,
                        "Ngày dự kiến trả không được ở trong quá khứ.",
                        "Lỗi ngày tháng", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy.",
                    "Lỗi ngày tháng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BorrowedBook bb = new BorrowedBook(selected, returnDate, selected.getPrice());
        currentBorrowing.getBooks().add(bb);

        int stt = currentBorrowing.getBooks().size();
        cartModel.addRow(new Object[]{
                stt, selected.getIsbn(), selected.getTitle(),
                selected.getAuthor(), String.format("%,.0f", selected.getPrice())
        });
        btnNext.setEnabled(currentBorrowing.getBooks().size() > 0);
    }
}
